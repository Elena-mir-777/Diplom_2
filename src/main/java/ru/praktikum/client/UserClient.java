package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.StellarBurgersRestClient;
import ru.praktikum.model.User;
import ru.praktikum.model.UserCredentials;
import ru.praktikum.model.UserResponse;
import static io.restassured.RestAssured.given;
public class UserClient extends StellarBurgersRestClient   {
    public String accessToken;
    @Step("Create {user}")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(user)
                .when()
                .post(BASE_URI+"auth/register")
                .then();
    }
    @Step("Log In {user}")
    public ValidatableResponse logInUser(UserCredentials userCredentials) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(userCredentials)
                .when()
                .post(BASE_URI+"auth/login")
                .then();
    }
    @Step("Get data Authorized {user}")
    public UserResponse getDataAuthorizedUser() {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .get(BASE_URI+"auth/user")
                .body().as(UserResponse.class);
    }
    @Step("Change data Authorized {user}")
    public UserResponse changeDataAuthorizedUser(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(BASE_URI+"auth/user")
                .body().as(UserResponse.class);
    }
    @Step("Change data Unauthorized {user}")
    public ValidatableResponse changeDataUnauthorizedUser(User user) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(user)
                .when()
                .patch(BASE_URI+"auth/user")
                .then();
    }
    @Step("Delete {user}")
    public ValidatableResponse deleteUser() {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .delete(BASE_URI+ "auth/user")
                .then();
    }
}
