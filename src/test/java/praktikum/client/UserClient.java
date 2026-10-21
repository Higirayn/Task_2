package praktikum.client;

import java.util.Map;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class UserClient {
    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(System.getProperty("baseUrl", "https://qa-stellarburgers.education-services.ru"))
            .setContentType(ContentType.JSON)
            .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", 10000).setParam("http.socket.timeout", 30000)))
            .build();

    @Step("Зарегистрировать пользователя")
    public Response register(Map<String, String> user) {
        return given().spec(spec).body(user).post("/api/auth/register");
    }

    @Step("Выполнить вход пользователя")
    public Response login(Map<String, String> credentials) {
        return given().spec(spec).body(credentials).post("/api/auth/login");
    }

    @Step("Изменить данные пользователя")
    public Response update(String token, Map<String, String> changes) {
        RequestSpecification request = given().spec(spec).body(changes);
        if (token != null) { request.header("Authorization", token); }
        return request.patch("/api/auth/user");
    }

    @Step("Удалить тестового пользователя")
    public Response delete(String token) {
        return given().spec(spec).header("Authorization", token).delete("/api/auth/user");
    }
}
