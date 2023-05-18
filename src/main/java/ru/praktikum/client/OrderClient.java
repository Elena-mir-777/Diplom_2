package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.StellarBurgersRestClient;
import java.io.File;
import static io.restassured.RestAssured.given;

public class OrderClient extends StellarBurgersRestClient {
    public String accessToken;
    @Step("Create orders from Authorized {user}")
    public ValidatableResponse createOrdersAuthorizedUser(File json) {
        return   given()
                .spec(getBaseRecSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .post(BASE_URI +  "orders")
                .then();
    }
    @Step("Create orders from Unauthorized {user}")
    public ValidatableResponse createOrdersUnauthorizedUser(File json) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(json)
                .when()
                .post(BASE_URI+"orders")
                .then();
    }
    @Step("Get orders from Authorized {user}")
    public ValidatableResponse getOrdersAuthorizedUser() {
        return given()
                .spec(getBaseRecSpec())
                .auth().oauth2(accessToken)
                .and()
                .when()
                .get(BASE_URI+"orders")
                .then();
    }
    @Step("Get orders from Unauthorized {user}")
    public ValidatableResponse getOrdersUnauthorizedUser() {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .when()
                .get(BASE_URI+"orders")
                .then();
    }
}
