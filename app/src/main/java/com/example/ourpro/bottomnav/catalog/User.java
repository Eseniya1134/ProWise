package com.example.ourpro.bottomnav.catalog;

public class User {
    private String username;
    private String email;

    public User() {} // Для Firebase

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
