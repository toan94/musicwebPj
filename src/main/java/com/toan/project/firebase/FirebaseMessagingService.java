package com.toan.project.firebase;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }


    public String sendNotification(Note note, String token) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject() + "by authorXXX")
                .setBody(note.getContent() + "NEW SONG!!!")
                .build();

//        Map<String, Map<String, String>> data = new HashMap<>();
//        data.put("display", note.getData();
        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();
        System.out.println(note.getData());



        return firebaseMessaging.send(message);
    }

    public TopicManagementResponse subscribe(String token) throws FirebaseMessagingException {
        List<String> tokens = Arrays.asList(token);
        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(
                tokens,
                "mytopic");

        return response;
    }

    public String sendNotificationToTopic(Note note) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .build();

//        Map<String, Map<String, String>> data = new HashMap<>();
//        data.put("display", note.getData();
        Message message = Message
                .builder()
                .setTopic("mytopic")
                .setNotification(notification)
                .putAllData(note.getData())
                .build();
        System.out.println(note.getData());



        return firebaseMessaging.send(message);
    }


}