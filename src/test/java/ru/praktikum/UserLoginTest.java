package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import ru.praktikum.model.User;
import ru.praktikum.model.UserCredentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class UserLoginTest extends BaseTest {
    @Test // Логин под существующим пользователем  (валидные email, password)
    @DisplayName("Check Existing User Can Login With Valid Data")
    public void checkUserExistingCanLoginWithValidData() {
        registerNewUser();
        ValidatableResponse response = userClient.login(UserCredentials.from(user))
                .assertThat().statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .and()
                .assertThat().body("user", notNullValue())
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("refreshToken", notNullValue());
        setToken(response);
    }
    @Test // Логин под существующим пользователем  (неверный password)
    @DisplayName("Check User Can Not Login With Wrong Password")
    public void checkUserCanNotLoginWithWrongPassword() {
        registerNewUser();
        user = new User(user.getEmail(),"123456",user.getName());
        ValidatableResponse response = userClient.login(UserCredentials.from(user))
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("email or password are incorrect", response.extract().path("message"));
        setToken(response);
    }
    @Test // Логин под существующим пользователем  (неверный email)
    @DisplayName("Check User Can Not Login With Wrong Email")
    public void checkUserCanNotLoginWithWrongEmail() {
        registerNewUser();
        user = new User("mihjrkonev77@yandex.ru",user.getPassword(),user.getName());
        ValidatableResponse response = userClient.login(UserCredentials.from(user))
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        assertEquals("email or password are incorrect", response.extract().path("message"));
        setToken(response);
    }

}
