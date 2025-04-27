package com.example.ourpro.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import com.example.ourpro.user.User;

public class ChatUtil {
    public static void createChat(User user){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        HashMap<String, String> chatInfo = new HashMap<>();
        chatInfo.put("user1", uid);
        chatInfo.put("user2", user.getUid());

        String chatId = generateChatId(uid, user.getUid());
        FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Chats").child(chatId)
                .setValue(chatInfo);

        addChatIdToUser(uid, chatId);
        addChatIdToUser(user.getUid(), chatId);
    }

    public static String generateChatId(String userId1, String userId2){
        String sumUser1User2 = userId1+userId2;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private static void addChatIdToUser(String uid, String chatId){
        FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Users").child(uid)
                .child("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String chats = task.getResult().getValue().toString();
                        String chatsUpd = addIdToStr(chats, chatId);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                .child("chats").setValue(chatsUpd);
                    }
                });
    }

    private static String addIdToStr(String str, String chatId){
        str += (str.isEmpty()) ? chatId : (","+chatId);
        return str;
    }
}