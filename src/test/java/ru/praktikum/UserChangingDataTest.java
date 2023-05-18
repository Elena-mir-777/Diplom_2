package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.model.*;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
public class UserChangingDataTest extends BaseTest {

    private static final String USER_NEW_EMAIL = "mironev77@mail.ru";
    private static final String USER_NEW_NAME = "Рыба моей мечты";

    @Test // получение информации о пользователе (авторизованный пользователь)
    @DisplayName("Check Get Data Authorized User")
    public void checkGetDataAuthorizedUser() {
       // регистрация нового пользователя и получение токена пользователя
       registerUser();
       // авторизация пользователя и получение нового токена пользователя
       authorizeUser();
       // запрос на получение данных об авторизованном пользователе
       UserResponse userResponse = userClient.getDataAuthorizedUser();
       // проверка тела ответа
       Assert.assertFalse(userResponse.getUser().getName().isEmpty());
       Assert.assertFalse(userResponse.getUser().getEmail().isEmpty());
       Assert.assertTrue(userResponse.isSuccess());
    }
    @Test //изменение информации о пользователе email, name (авторизованный пользователь)
    @DisplayName("Check Patch Data Authorized User")
    public void checkPatchDataAuthorizedUser() {
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя и получение нового токена пользователя
        authorizeUser();
        //изменение информации о пользователе email, name
        user.setEmail(USER_NEW_EMAIL);
        user.setName(USER_NEW_NAME);
        //проверка обновления данных об авторизованном пользователе
        UserResponse resp = userClient.changeDataAuthorizedUser(user);
        Assert.assertEquals(USER_NEW_EMAIL,resp.getUser().getEmail());
        Assert.assertEquals(USER_NEW_NAME,resp.getUser().getName());
        // получение обновленных данных о пользователе
        UserResponse userResponse = userClient.getDataAuthorizedUser();
        Assert.assertEquals(USER_NEW_EMAIL,userResponse.getUser().getEmail());
        Assert.assertEquals(USER_NEW_NAME,userResponse.getUser().getName());
    }
    @Test // изменение информации о пользователе email, name (неавторизованный пользователь)
    @DisplayName("Check Get Data Unauthorized User")
    public void checkGetDataUnauthorizedUser() {
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // получаем токен пользователя
        setToken(response);
        // из ответа получаем Имя и Email неавторизованного пользователя
        UserResponse ur = response.extract().body().as(UserResponse.class);
        String originalName = ur.getUser().getName();
        String originalEmail = ur.getUser().getEmail();
        //изменение информации о пользователе email, name
        user.setEmail(USER_NEW_EMAIL);
        user.setName(USER_NEW_NAME);
        //проверка обновления данных об неавторизованном пользователе
        ValidatableResponse resp = userClient.changeDataUnauthorizedUser(user);
        Assert.assertEquals(SC_UNAUTHORIZED,resp.extract().statusCode());
        Assert.assertEquals("You should be authorised",resp.extract().path("message"));
        // проверка получения обновленных данных о пользователе
        UserResponse userResponse = userClient.getDataAuthorizedUser();
        Assert.assertEquals(originalEmail, userResponse.getUser().getEmail());
        Assert.assertEquals(originalName,userResponse.getUser().getName());
    }
}
