package praktikum.tests;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import praktikum.model.TestUser;
import praktikum.support.BaseApiTest;
import static org.hamcrest.Matchers.*;

@DisplayName("Создание пользователя")
class CreateUserTest extends BaseApiTest {
    @Test
    @DisplayName("Регистрация уникального пользователя")
    void createsUniqueUser() {
        TestUser user = TestUser.unique();
        register(user.registration()).then().statusCode(200)
                .body("success", is(true)).body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer ")).body("refreshToken", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Повторная регистрация возвращает ошибку")
    void rejectsDuplicateUser() {
        TestUser user = TestUser.unique();
        createUser(user);
        assertError(register(user.registration()), 403, "User already exists");
    }

    @ParameterizedTest(name = "Обязательное поле {0}, пустое значение: {1}")
    @CsvSource({"email, false", "password, false", "name, false",
                "email, true", "password, true", "name, true"})
    void rejectsMissingRequiredField(String field, boolean empty) {
        Map<String, String> body = new HashMap<>(TestUser.unique().registration());
        if (empty) { body.put(field, ""); } else { body.remove(field); }
        assertError(register(body), 403, "Email, password and name are required fields");
    }
}
