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
        user = UserGenerator.getRandom();
    }
    @After
    public void clearData() {
        if (userClient.accessToken != null) {
            userClient.deleteUser();
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
    protected void registerUser() {
        ValidatableResponse response = userClient.createUser(user);
        setToken(response);
    }
    // метод авторизации пользователя
    protected void authorizeUser(){
        ValidatableResponse response = userClient.logInUser(UserCredentials.from(user));
        setToken(response);
    }
}

