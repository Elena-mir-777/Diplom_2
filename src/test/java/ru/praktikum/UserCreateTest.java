package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import ru.praktikum.model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
public class UserCreateTest extends BaseTest {
    @Test // Создание уникального пользователя (валидные email, password и name)
    @DisplayName("Check User Can Be Create With Valid Data")
    public void checkUserCreateWithValidData() {
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // получаем токен  пользователя
        setToken(response);
        // проверяем статус-код 200 и тело ответа
        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat().body("success", is(true))
                .assertThat().body("user", notNullValue())
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("refreshToken", notNullValue());
    }

    @Test // Создание пользователя, который уже зарегистрирован (валидные email, password и name)
    @DisplayName("Check User Who Already Registered Can Not Created")
    public void checkUserAlreadyRegisterCanNotCreated() {
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // получаем токен  пользователя
        setToken(response);
        // регистрируем повторно пользователя
        ValidatableResponse resp = userClient.createUser(user);
        // проверяем, что пользователь не зарегистрировался второй раз, токен = null
        String token = resp.extract().path("accessToken");
        Assert.assertNull(token);
        // проверяем статус-код 403 и тело ответа
        resp.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("User already exists", resp.extract().path("message"));
    }

    @Test // Создание пользователя - не заполнено  обязательное поле email
    @DisplayName("Check User Can Not Be Created Without Email")
    public void checkUserCanNotCreateWithoutEmail() {
        // создаем нового пользователя - поле email не заполнено
        user = new User("",user.getPassword(), user.getName());
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // попробуем получить токен. Хотя он долже быть null (проверка ниже), но на случай если он не null,
        // надо его запомнить чтобы потом удалить
        setToken(response);
        // проверяем, что пользователь не зарегистрировался, токен = null
        Assert.assertNull(userClient.accessToken);
        //проверяем статус-код 403 и тело ответа
        response.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("Email, password and name are required fields", response.extract().path("message"));
   }
    @Test // Создание пользователя - не заполнено  обязательное поле password
    @DisplayName("Check User Can Not Be Created Without Password")
    public void checkUserCanNotCreateWithoutPassword() {
        // создаем нового пользователя - поле password не заполнено
        user = new User(user.getEmail(),"", user.getName());
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // попробуем получить токен. Хотя он долже быть null (проверка ниже), но на случай если он не null,
        // надо его запомнить чтобы потом удалить
        setToken(response);
        // проверяем, что пользователь не зарегистрировался, токен = null
        Assert.assertNull(userClient.accessToken);
        //проверяем статус-код 403 и тело ответа
        response.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("Email, password and name are required fields", response.extract().path("message"));
    }
    @Test // Создание пользователя - не заполнено обязательное поле name
    @DisplayName("Check User Can Not Be Created Without Name")
    public void checkUserCanNotCreateWithoutName() {
        // создаем нового пользователя - поле name не заполнено
        user = new User(user.getEmail(),user.getPassword(),"");
        // регистрируем нового пользователя
        ValidatableResponse response = userClient.createUser(user);
        // попробуем получить токен. Хотя он долже быть null (проверка ниже), но на случай если он не null,
        // надо его запомнить чтобы потом удалить
        setToken(response);
        // проверяем, что пользователь не зарегистрировался, токен = null
        Assert.assertNull(userClient.accessToken);
        //проверяем статус-код 403 и тело ответа
        response.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", is(false))
                .and()
                .assertThat().body("message", notNullValue());
        Assert.assertEquals("Email, password and name are required fields", response.extract().path("message"));
    }
}
