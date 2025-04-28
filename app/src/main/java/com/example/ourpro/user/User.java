package com.example.ourpro.user;

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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
