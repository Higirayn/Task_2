package praktikum.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import praktikum.client.UserClient;
import praktikum.client.OrderClient;
import praktikum.model.TestUser;
import static org.hamcrest.Matchers.*;

public abstract class BaseApiTest {
    protected final UserClient users = new UserClient();
    protected final OrderClient orders = new OrderClient();
    private final List<String> tokens = new ArrayList<>();

    @Step("Зарегистрировать пользователя с последующим удалением")
    protected Response register(Map<String, String> body) {
        Response response = users.register(body);
        if (response.statusCode() == 200 && response.jsonPath().getString("accessToken") != null) {
            tokens.add(response.jsonPath().getString("accessToken"));
        }
        return response;
    }

    @Step("Подготовить тестового пользователя")
    protected String createUser(TestUser user) {
        return register(user.registration()).then().statusCode(200).body("success", is(true))
                .extract().path("accessToken");
    }

    @Step("Проверить код и тело ошибки")
    protected void assertError(Response response, int code, String message) {
        response.then().statusCode(code).body("success", is(false)).body("message", equalTo(message));
    }

    @AfterEach
    @Step("Удалить созданных в тесте пользователей")
    void deleteUsers() {
        RuntimeException failure = null;
        for (String token : tokens) {
            try {
                users.delete(token).then().statusCode(202).body("success", is(true));
            } catch (RuntimeException | AssertionError e) {
                if (failure == null) { failure = new IllegalStateException("Не удалось удалить тестового пользователя", e); }
                else { failure.addSuppressed(e); }
            }
        }
        if (failure != null) { throw failure; }
    }
}
