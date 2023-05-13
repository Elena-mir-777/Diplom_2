package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.StellarBurgersRestClient;
import ru.praktikum.model.User;
import ru.praktikum.model.UserCredentials;
import ru.praktikum.model.UserResponse;

import static io.restassured.RestAssured.given;
public class UserClient extends StellarBurgersRestClient   {
    private static final String USER_URI = BASE_URI +  "auth/";
    public String accessToken;
    public UserClient() {
        RestAssured.baseURI = BASE_URI;
    }
    @Step("Create user {user}")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(user)
                .when()
                .post(USER_URI+"register")
                .then();
    }
    @Step("Login user {user}")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(userCredentials)
                .when()
                .post(USER_URI+"login")
                .then();
    }
    @Step("Get date Authorized user {user}")
    public UserResponse getData() {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .get(USER_URI+"user")
                .body().as(UserResponse.class);
    }
    @Step("Data update Authorized User {user}")
    public UserResponse dataUpdate(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_URI+"user")
                .body().as(UserResponse.class);
    }
    @Step("Data update Unauthorized User {user}")
    public ValidatableResponse update(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(user)
                .when()
                .patch(USER_URI+"user")
                .then();
    }
    @Step("Delete user {user}")
    public ValidatableResponse delete() {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .delete(USER_URI+ "user")
                .then();

    }
}
