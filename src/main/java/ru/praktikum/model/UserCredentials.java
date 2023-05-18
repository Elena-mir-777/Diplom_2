package ru.praktikum.model;
public class UserCredentials {
    private final String email;
    private final String password;
    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public static UserCredentials from(User user){
        return new UserCredentials(user.getEmail(), user.getPassword());
    }
}
