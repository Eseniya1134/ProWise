package com.example.ourpro.bottomnav.catalog;

public class User {
    private String username;
    private String email;
    private String uid;

    // Пустой конструктор нужен Firebase
    public User() {}

    // Конструктор для ручного создания User объектов
    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
