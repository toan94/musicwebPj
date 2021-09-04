package com.toan.project.firebase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FireBaseTestController {

    @Autowired
    FirebaseMessagingService firebaseService;

    /*******************************/

    @RequestMapping("/send-notification")
    @ResponseBody
    public String sendNotification(@RequestBody Note note,
                                   @RequestParam String token) throws FirebaseMessagingException {
        return firebaseService.sendNotification(note, token);
    }

    @RequestMapping("/send-notification-to-topic")
    @ResponseBody
    public String sendNotificationToTopic(@RequestBody Note note) throws FirebaseMessagingException {
        return firebaseService.sendNotificationToTopic(note);
    }

    @RequestMapping("/sub")
    @ResponseBody
    public String sub(@RequestParam String token) throws FirebaseMessagingException {
        TopicManagementResponse t = firebaseService.subscribe(token);
        System.out.println(t);
        return "ok man subbed";
    }
}

