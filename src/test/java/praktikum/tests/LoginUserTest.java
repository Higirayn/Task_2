package praktikum.tests;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import praktikum.model.TestUser;
import praktikum.support.BaseApiTest;
import static org.hamcrest.Matchers.*;

@DisplayName("Вход пользователя")
class LoginUserTest extends BaseApiTest {
    private TestUser user;

    @BeforeEach
    void prepareUser() {
        user = TestUser.unique();
        createUser(user);
    }

    @Test
    @DisplayName("Вход существующего пользователя")
    void logsInExistingUser() {
        users.login(user.credentials()).then().statusCode(200).body("success", is(true))
                .body("user.email", equalTo(user.getEmail())).body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer ")).body("refreshToken", not(emptyOrNullString()));
    }

    @ParameterizedTest(name = "Неверная почта: {0}, неверный пароль: {1}")
    @CsvSource({"true, false", "false, true", "true, true"})
    void rejectsInvalidCredentials(boolean wrongEmail, boolean wrongPassword) {
        Map<String, String> credentials = Map.of(
                "email", wrongEmail ? TestUser.unique().getEmail() : user.getEmail(),
                "password", wrongPassword ? "WrongPassword123!" : user.getPassword());
        assertError(users.login(credentials), 401, "email or password are incorrect");
    }
}
