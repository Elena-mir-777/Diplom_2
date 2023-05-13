package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.StellarBurgersRestClient;
import java.io.File;
import static io.restassured.RestAssured.given;
public class OrderClient extends StellarBurgersRestClient {
    private static final String ORDER_URI = BASE_URI +  "orders";
    public String accessToken;
    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }
    @Step("Create orders {user}")
    public ValidatableResponse createOrders(File json) {
        return given()
                .spec(getBaseRecSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Create orders By Unauthorized {user}")
    public ValidatableResponse createOrdersByUnauthorizedUser(File json) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(json)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Receiving orders from authorized {user}")
    public ValidatableResponse receivingOrdersFromAuthorized() {
        return given()
                .spec(getBaseRecSpec())
                .auth().oauth2(accessToken)
                .and()
                .when()
                .get(ORDER_URI)
                .then();
    }
    @Step("Receiving orders from Unauthorized {user}")
    public ValidatableResponse receivingOrdersFromUnAuthorized() {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .when()
                .get(ORDER_URI)
                .then();
    }
}
