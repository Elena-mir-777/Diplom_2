package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.client.OrderClient;
import java.io.File;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
    @DisplayName("Check Success Create Order By Authorized User")
    public void checkSuccessCreateOrderAuthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя и получение нового токена пользователя
        authorizeUser();
        //проверка статуса кода 200 и тела ответа
        orderClient.createOrdersAuthorizedUser(json)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .and()
                .assertThat().body("name", notNullValue())
                .assertThat().body("order", notNullValue());
        }
    @Test // Создание заказа с валидными ингридиентами неавторизованным пользователем
    @DisplayName("Check Failure Create Order By Unauthorized User")
    public void checkFailureCreateOrderUnauthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // проверка статуса кода 401 и тела ответа
        // должна быть ошибка, что пользователь не авторизован
        // пользователь будет удален в @After потому что токен был получен в registerUser
        ValidatableResponse resp = orderClient.createOrdersUnauthorizedUser(json)
               .assertThat()
               .statusCode(SC_UNAUTHORIZED)
               .and()
               .assertThat().body("success", is(false))
               .and()
               .assertThat().body("message", notNullValue());
        Assert.assertEquals("You should be authorised", resp.extract().path("message"));
    }
    @Test // Создание заказа без ингридиентов авторизованным пользователем
    @DisplayName("Check Failure Create Order Without Ingredients By Authorized User")
    public void checkFailureCreateOrderWithoutIngredientsAuthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя и получение нового токена пользователя
        authorizeUser();
        //проверка статуса кода 400 и тела ответа
        json = jsonNull;
        ValidatableResponse resp = orderClient.createOrdersAuthorizedUser(json)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("Ingredient ids must be provided", resp.extract().path("message"));
    }
    @Test // Создание заказа: с невалидным хеш ингридиентов авторизованным пользователем
    @DisplayName("Check Failure Create Order With Invalid Ingredients By Authorized User")
    public void checkFailureCreateOrderWithInvalidIngredientsAuthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя и получение нового токена пользователя
        authorizeUser();
        //проверка статуса кода 500
        json = jsonInvalid;
        orderClient.createOrdersAuthorizedUser(json)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
    @Test // Получение заказов авторизованного пользователя
    @DisplayName("Check Success Get Orders From Authorized User")
    public void checkSuccessGetOrdersAuthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя и получение нового токена пользователя
        authorizeUser();
        // создание заказа авторизованным пользователем
        orderClient.createOrdersAuthorizedUser(json);
        // проверка статус-кода 200 и тела ответа
        orderClient.getOrdersAuthorizedUser()
                .assertThat().statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .assertThat().body("orders",notNullValue())
                .assertThat().body("total",notNullValue())
                .assertThat().body("totalToday",notNullValue());
    }

    @Test // Получение заказов неавторизованного пользователя
    @DisplayName("Check Failure Get Orders From Unauthorized User")
    public void checkFailureGetOrdersUnauthorizedUser(){
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // создание заказа неавторизованным пользователем
        orderClient.createOrdersUnauthorizedUser(json);
        // проверка статус-кода 401 и тела ответа
        ValidatableResponse resp = orderClient.getOrdersUnauthorizedUser()
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("You should be authorised", resp.extract().path("message"));
    }
}



