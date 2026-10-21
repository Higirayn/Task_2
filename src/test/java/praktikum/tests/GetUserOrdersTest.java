package praktikum.tests;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.TestUser;
import praktikum.support.BaseApiTest;
import static org.hamcrest.Matchers.*;

@DisplayName("Получение заказов пользователя")
class GetUserOrdersTest extends BaseApiTest {
    @Test
    @DisplayName("Пользователь получает только свои заказы")
    void returnsOnlyOwnOrders() {
        String token = createUser(TestUser.unique());
        String otherToken = createUser(TestUser.unique());
        List<String> ingredients = orders.ingredientIds().subList(0, 2);
        int ownNumber = orders.create(token, Map.of("ingredients", ingredients)).then()
                .statusCode(200).body("success", is(true)).extract().path("order.number");
        int otherNumber = orders.create(otherToken, Map.of("ingredients", ingredients)).then()
                .statusCode(200).body("success", is(true)).extract().path("order.number");
        orders.get(token).then().statusCode(200).body("success", is(true))
                .body("orders", hasSize(1)).body("orders.number", hasItem(ownNumber))
                .body("orders.number", not(hasItem(otherNumber)))
                .body("orders[0].ingredients", equalTo(ingredients));
    }

    @Test
    @DisplayName("У нового пользователя нет заказов")
    void returnsEmptyOrdersForNewUser() {
        String token = createUser(TestUser.unique());
        orders.get(token).then().statusCode(200).body("success", is(true)).body("orders", empty());
    }

    @Test
    @DisplayName("Без авторизации заказы недоступны")
    void rejectsRequestWithoutAuthorization() {
        assertError(orders.get(null), 401, "You should be authorised");
    }
}
