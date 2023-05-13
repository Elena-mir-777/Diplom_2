package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.client.OrderClient;
import ru.praktikum.model.UserGenerator;
import java.io.File;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
public class OrderCreateTest extends BaseTest{
    private OrderClient orderClient;
    private File json = new File("src/test/resources/newIngredients");
    private final File jsonNull = new File("src/test/resources/nullIngredients");
    private final File jsonInvalid = new File("src/test/resources/invalidIngredients");
    @Before
    @Override
    public void setUp() {
        super.setUp();
        orderClient = new OrderClient();
    }
    //получение токена пользователя
    @Override
    protected void setToken(ValidatableResponse response) {
        super.setToken(response);
        orderClient.accessToken = userClient.accessToken;
    }
    @Test // Создание заказа c валидными ингридиентами авторизованным пользователем
    @DisplayName("Check Success Create Order By AuthorizedUser")
    public void checkSuccessCreateOrderByAuthorizedUser(){
        registerNewUser();
        authorizedUser();
        orderClient.createOrders(json)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .and()
                .assertThat().body("name", notNullValue())
                .assertThat().body("order", notNullValue());
        }
    @Test // Создание заказа с валидными ингридиентами без авторизации
    @DisplayName("Check Failure Create Order By Unauthorized User")
    public void checkFailureCreateOrdersByUnauthorizedUser(){
        user = UserGenerator.getRandom();
        ValidatableResponse response = userClient.create(user);
        setToken(response);
        ValidatableResponse resp = orderClient.createOrdersByUnauthorizedUser(json)
               .assertThat()
                // должна быть ошибка что пользователь не авторизован
               .statusCode(SC_UNAUTHORIZED)
               .and()
               .assertThat().body("success", is(false))
               .and()
               .assertThat().body("message", notNullValue());
        assertEquals("You should be authorised", resp.extract().path("message"));
    }
    @Test // Создание заказа без ингридиентов авторизованным пользователем
    @DisplayName("Check Failure Create Order WithOut Ingredients By Authorized User")
    public void checkFailureCreateOrderWithOutIngredientsByAuthorizedUser(){
        registerNewUser();
        authorizedUser();
        json = jsonNull;
        ValidatableResponse resp = orderClient.createOrders(json)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("Ingredient ids must be provided", resp.extract().path("message"));
    }
    @Test // Создание заказа: с невалидный хеш ингридиентов авторизованным пользователем
    @DisplayName("Check Failure Create Order With Invalid Ingredients By Authorized User")
    public void checkFailureCreateOrderWithInvalidIngredientsByAuthorizedUser(){
        registerNewUser();
        authorizedUser();
        json = jsonInvalid;
        orderClient.createOrders(json)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
    @Test // Получение заказов авторизованного пользователя
    @DisplayName("Check Success Receiving Order From Authorized User")
    public void checkSuccessReceivingOrdersFromAuthorizedUser(){
        registerNewUser();
        authorizedUser();
        orderClient.createOrders(json);
        orderClient.receivingOrdersFromAuthorized()
                .assertThat().statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .assertThat().body("orders",notNullValue())
                .assertThat().body("total",notNullValue())
                .assertThat().body("totalToday",notNullValue());
    }

    @Test // Получение заказов неавторизованного пользователя
    public void receivingOrdersFromUnAuthorizedUser(){
        user = UserGenerator.getRandom();
        ValidatableResponse response = userClient.create(user);
        setToken(response);
        orderClient.createOrdersByUnauthorizedUser(json);
        ValidatableResponse resp = orderClient.receivingOrdersFromUnAuthorized()
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .assertThat().body("message", notNullValue());
        assertEquals("You should be authorised", resp.extract().path("message"));
    }
}



