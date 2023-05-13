package ru.praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import ru.praktikum.client.UserClient;
import ru.praktikum.model.User;
import ru.praktikum.model.UserCredentials;
import ru.praktikum.model.UserGenerator;

public class BaseTest {
    protected UserClient userClient;
    protected User user;
    @Before
    public void setUp() {
        userClient = new UserClient();
    }
    @After
    public void clearData() {
        if (userClient.accessToken != null) {
            userClient.delete();
        }
    }
    // метод получения токена пользователя
    protected void setToken(ValidatableResponse response) {
        String accessToken = response.extract().path("accessToken");
        if (accessToken != null) {
            userClient.accessToken = accessToken.substring(7);
        }
    }
    // метод регистрации нового пользователя
    protected void registerNewUser(){
        user = UserGenerator.getRandom();
        userClient.create(user);
    }
    // метод авторизации пользователя
    protected void authorizedUser(){
        ValidatableResponse response = userClient.login(UserCredentials.from(user));
        setToken(response);
    }
}
