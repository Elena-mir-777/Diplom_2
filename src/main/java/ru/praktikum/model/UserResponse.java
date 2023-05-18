package ru.praktikum.model;

public class UserResponse {
    private boolean success;
    private UserData user;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public UserData getUser() {
        return user;
    }
    public void setUser(UserData user) {
        this.user = user;
    }
}
