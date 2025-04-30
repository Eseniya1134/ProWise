package com.example.ourpro.chats;

public class Chat {
    private String chat_id, chat_name, userId1, userId2;

    public Chat(String userId2, String userId1, String chat_name, String chat_id) {
        this.userId2 = userId2;
        this.userId1 = userId1;
        this.chat_name = chat_name;
        this.chat_id = chat_id;
    }
    //Chat chat = new Chat(userId2, userId1, chatName, chatId);

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }
}
