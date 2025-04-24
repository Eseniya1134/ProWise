package com.example.ourpro.bottomnav.catalog;

public class User {
    private String username;
    private String email;

    public User() {
        // Пустой конструктор нужен для Firebase
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
