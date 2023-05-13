package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.model.*;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
public class UserChangingDataTest extends BaseTest{
    @Test // получение информации о пользователе (авторизованный пользователь)
    @DisplayName("Check Get Data Authorized User")
    public void checkGetDataAuthorizedUser() {
       registerNewUser();
       authorizedUser();
       UserResponse userResponse = userClient.getData();
       Assert.assertFalse(userResponse.getUser().getName().isEmpty());
       Assert.assertFalse(userResponse.getUser().getEmail().isEmpty());
       Assert.assertTrue(userResponse.isSuccess());
    }
    @Test //изменение информации о пользователе email, name (авторизованный пользователь)
    @DisplayName("Check Patch Data Authorized User")
    public void checkPatchDataAuthorizedUser() {
        registerNewUser();
        authorizedUser();
        user.setEmail("mironev77@mail.ru");
        user.setName("Рыба моей мечты");

        UserResponse userResponse = userClient.dataUpdate(user);
        Assert.assertEquals("mironev77@mail.ru",userResponse.getUser().getEmail());
        Assert.assertEquals("Рыба моей мечты",userResponse.getUser().getName());

        userResponse = userClient.getData();
        Assert.assertEquals("mironev77@mail.ru",userResponse.getUser().getEmail());
        Assert.assertEquals("Рыба моей мечты",userResponse.getUser().getName());
    }
    @Test // изменение информации о пользователе email, name (неавторизованный пользователь)
    @DisplayName("Check Get Data Unauthorized User")
    public void checkGetDataUnauthorizedUser() {
        user = UserGenerator.getRandom();

        ValidatableResponse response = userClient.create(user);
        setToken(response);

        UserResponse ur = response.extract().body().as(UserResponse.class);
        String originalName = ur.getUser().getName();
        String originalEmail = ur.getUser().getEmail();

        user.setEmail("mironev77@mail.ru");
        user.setName("Рыба моей мечты");

        ValidatableResponse resp = userClient.update(user);

        Assert.assertEquals(SC_UNAUTHORIZED,resp.extract().statusCode());
        Assert.assertEquals("You should be authorised",resp.extract().path("message"));

        UserResponse userResponse = userClient.getData();
        Assert.assertEquals(originalEmail, userResponse.getUser().getEmail());
        Assert.assertEquals(originalName,userResponse.getUser().getName());

    }
}
