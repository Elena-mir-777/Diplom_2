package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Test;
import ru.praktikum.model.User;
import ru.praktikum.model.UserCredentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest extends BaseTest {
    @Test // Логин под существующим пользователем  (валидные email, password)
    @DisplayName("Check Existing User Can Login With Valid Data")
    public void checkUserExistingCanLoginWithValidData() {
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // авторизация пользователя
        ValidatableResponse resp = userClient.logInUser(UserCredentials.from(user));
        // проверяем статус-код 200 и тело ответа
        resp.assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true)) // проверяем тело ответа
                .and()
                .assertThat().body("user", notNullValue())
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("refreshToken", notNullValue());
    }
    @Test // Логин под существующим пользователем  (неверный password)
    @DisplayName("Check User Can Not Login With Wrong Password")
    public void checkUserCanNotLoginWithWrongPassword() {
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // передаем неверный пароль
        user = new User(user.getEmail(),"123456",user.getName());
        // авторизуемся с неверным паролем
        ValidatableResponse resp = userClient.logInUser(UserCredentials.from(user));
        // проверяем, что пользователь не авторизовался, токен = null
        String loginToken = resp.extract().path("accessToken");
        Assert.assertNull(loginToken);
        // проверяем статус-код 401 и тело ответа
        resp.assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("email or password are incorrect", resp.extract().path("message"));
    }
    @Test // Логин под существующим пользователем  (неверный email)
    @DisplayName("Check User Can Not Login With Wrong Email")
    public void checkUserCanNotLoginWithWrongEmail() {
        // регистрация нового пользователя и получение токена пользователя
        registerUser();
        // передаем неверный email
        user = new User("mihjrkonev77@yandex.ru",user.getPassword(),user.getName());
        // авторизуемся с неверным email
        ValidatableResponse resp = userClient.logInUser(UserCredentials.from(user));
        // проверяем, что пользователь не авторизовался, токен = null
        String loginToken = resp.extract().path("accessToken");
        Assert.assertNull(loginToken);
        // проверяем статус-код 401 и тело ответа
                resp.assertThat().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("email or password are incorrect", resp.extract().path("message"));
    }
}
