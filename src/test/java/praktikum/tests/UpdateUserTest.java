package praktikum.tests;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import praktikum.model.TestUser;
import praktikum.support.BaseApiTest;
import static org.hamcrest.Matchers.*;

@DisplayName("Изменение данных пользователя")
class UpdateUserTest extends BaseApiTest {
    private TestUser user;
    private String token;

    @BeforeEach
    void prepareUser() {
        user = TestUser.unique();
        token = createUser(user);
    }

    private String newValue(String field) {
        if (field.equals("email")) { return TestUser.unique().getEmail(); }
        return field.equals("password") ? "UpdatedPassword123!" : "Новое имя";
    }

    @ParameterizedTest(name = "Изменение поля {0} с авторизацией")
    @ValueSource(strings = {"email", "name", "password"})
    void updatesFieldWithAuthorization(String field) {
        String value = newValue(field);
        Map<String, String> expected = new HashMap<>(user.registration());
        expected.put(field, value);
        users.update(token, Map.of(field, value)).then().statusCode(200).body("success", is(true))
                .body("user.email", equalTo(expected.get("email")))
                .body("user.name", equalTo(expected.get("name")));
        users.login(Map.of("email", expected.get("email"), "password", expected.get("password")))
                .then().statusCode(200).body("success", is(true))
                .body("user.email", equalTo(expected.get("email")))
                .body("user.name", equalTo(expected.get("name")));
    }

    @ParameterizedTest(name = "Изменение поля {0} без авторизации")
    @ValueSource(strings = {"email", "name", "password"})
    void rejectsUpdateWithoutAuthorization(String field) {
        assertError(users.update(null, Map.of(field, newValue(field))), 401, "You should be authorised");
        users.login(user.credentials()).then().statusCode(200).body("success", is(true))
                .body("user.email", equalTo(user.getEmail())).body("user.name", equalTo(user.getName()));
    }
}
