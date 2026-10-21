package praktikum.client;

import java.util.List;
import java.util.Map;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class OrderClient {
    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(System.getProperty("baseUrl", "https://qa-stellarburgers.education-services.ru"))
            .setContentType(ContentType.JSON)
            .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", 10000).setParam("http.socket.timeout", 30000)))
            .build();

    @Step("Получить актуальные идентификаторы ингредиентов")
    public List<String> ingredientIds() {
        return given().spec(spec).get("/api/ingredients").then()
                .statusCode(200).body("success", org.hamcrest.Matchers.is(true))
                .extract().jsonPath().getList("data._id", String.class);
    }

    @Step("Создать заказ")
    public Response create(String token, Map<String, ?> body) {
        RequestSpecification request = given().spec(spec).body(body);
        if (token != null) { request.header("Authorization", token); }
        return request.post("/api/orders");
    }

    @Step("Получить заказы пользователя")
    public Response get(String token) {
        RequestSpecification request = given().spec(spec);
        if (token != null) { request.header("Authorization", token); }
        return request.get("/api/orders");
    }
}
