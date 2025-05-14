package com.example.ourpro.message;

import com.google.firebase.Timestamp;

public class Message {
    private String id;
    private String senderId;
    private String text;
    private Timestamp date;

    public Message() {} // Обязательно нужен для Firestore

    public Message(String id, String senderId, String text, Timestamp date) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public Timestamp getDate() {
        return date;
    }

}
