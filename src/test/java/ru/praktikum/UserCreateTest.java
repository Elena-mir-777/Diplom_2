package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.model.User;
import ru.praktikum.model.UserGenerator;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
public class UserCreateTest extends BaseTest {
    @Test // Создание уникального пользователя (валидные email, password и name)
    @DisplayName("Check User Can Be Created With Valid Data")
    public void checkUserCanBeCreatedWithValidData() {
        user = UserGenerator.getRandom();
        ValidatableResponse response = userClient.create(user)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .and()
                .assertThat().body("user", notNullValue())
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("refreshToken", notNullValue());
        setToken(response);
    }
    @Test // Создание пользователя, который уже существует (валидные email, password и name)
    @DisplayName("Check User Who Already Registered Can Not Created")
    public void checkUserWhoAlreadyRegisteredCanNotCreated() {
        user = UserGenerator.getRandom();
        userClient.create(user);
        ValidatableResponse response = userClient.create(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("User already exists", response.extract().path("message"));
        setToken(response);
    }
    @Test // Создание пользователя - не заполнено  обязательное поле email
    @DisplayName("Check User Can Not Be Created Without Email")
    public void checkUserCanNotBeCreatedWithoutEmail() {
        user = UserGenerator.getRandom();
        user = new User("",user.getPassword(), user.getName());
        ValidatableResponse response = userClient.create(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("Email, password and name are required fields", response.extract().path("message"));
        setToken(response);
    }
    @Test // Создание пользователя - не заполнено  обязательное поле password
    @DisplayName("Check User Can Not Be Created Without Password")
    public void checkUserCanNotBeCreatedWithoutPassword() {
        user = UserGenerator.getRandom();
        user = new User(user.getEmail(),"", user.getName());
        ValidatableResponse response = userClient.create(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("Email, password and name are required fields", response.extract().path("message"));
        setToken(response);
    }
    @Test // Создание пользователя - не заполнено обязательное поле name
    @DisplayName("Check User Can Not Be Created Without Name")
    public void checkUserCanNotBeCreatedWithoutName() {
        user = UserGenerator.getRandom();
        user = new User(user.getEmail(),user.getPassword(),"");
        ValidatableResponse response = userClient.create(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("Email, password and name are required fields", response.extract().path("message"));
        setToken(response);
    }
}
