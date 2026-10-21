package praktikum.tests;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import praktikum.model.TestUser;
import praktikum.support.BaseApiTest;
import static org.hamcrest.Matchers.*;

@DisplayName("Создание заказа")
class CreateOrderTest extends BaseApiTest {
    private String token(boolean authorized) {
        return authorized ? createUser(TestUser.unique()) : null;
    }

    @ParameterizedTest(name = "Заказ с ингредиентами, авторизация: {0}")
    @ValueSource(booleans = {true, false})
    void createsOrderWithIngredients(boolean authorized) {
        String token = token(authorized);
        List<String> ingredients = orders.ingredientIds().subList(0, 2);
        orders.create(token, Map.of("ingredients", ingredients)).then().statusCode(200)
                .body("success", is(true)).body("name", not(emptyOrNullString()))
                .body("order.number", greaterThan(0));
    }

    @ParameterizedTest(name = "Пустой список ингредиентов, авторизация: {0}")
    @ValueSource(booleans = {true, false})
    void rejectsEmptyIngredients(boolean authorized) {
        assertError(orders.create(token(authorized), Map.of("ingredients", Collections.emptyList())),
                400, "Ingredient ids must be provided");
    }

    @ParameterizedTest(name = "Поле ingredients отсутствует, авторизация: {0}")
    @ValueSource(booleans = {true, false})
    void rejectsMissingIngredients(boolean authorized) {
        assertError(orders.create(token(authorized), Collections.emptyMap()),
                400, "Ingredient ids must be provided");
    }

    @ParameterizedTest(name = "Невалидный хеш ингредиента, авторизация: {0}")
    @ValueSource(booleans = {true, false})
    void rejectsInvalidIngredientHash(boolean authorized) {
        orders.create(token(authorized), Map.of("ingredients", List.of("invalid-ingredient-hash")))
                .then().statusCode(500).body(containsString("Internal Server Error"));
    }
}
