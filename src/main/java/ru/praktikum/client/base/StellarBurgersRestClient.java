package ru.praktikum.client.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class StellarBurgersRestClient {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site/api/";
    protected RequestSpecification getBaseRecSpec(){
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }

}
