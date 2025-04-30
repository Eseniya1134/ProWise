package com.example.ourpro.utils;

import android.util.Log;

import com.example.ourpro.chats.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import com.example.ourpro.user.User;

public class ChatUtil {

    private static final String TAG = "Upload ###";
    public static void createChat(User user){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        HashMap<String, String> chatInfo = new HashMap<>();
        chatInfo.put("user1", uid);
        chatInfo.put("user2", user.getUid());
        //  Log.e(TAG, "Айди пользователя: " + uid + "," + user.getUid());


        String chatId = generateChatId(uid, user.getUid());
        FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Chats").child(chatId)
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
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
                db.getReference("Users").child(uid)
                .child("chats").get().addOnSuccessListener(snapshot -> {
                String chats = snapshot.getValue(String.class);
                if (repetitionСheck(chats, chatId) == true){
                    chats = addIdToStr(chats, chatId);
                }
                db.getReference("Users").child(uid).child("chats").setValue(chats);});
    }

    private static String addIdToStr(String str, String chatId){
        str += (str.isEmpty()) ? chatId : (","+chatId);
        return str;
    }

    private static boolean repetitionСheck (String str, String chatId) {
        String[] chatIds = str.split(",");
        for (String id : chatIds) {
            if ( chatId.equals(id) )
                return false;
        }
        return true;
    }

}