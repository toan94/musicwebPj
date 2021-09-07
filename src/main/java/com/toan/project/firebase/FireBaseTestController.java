package com.toan.project.firebase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.toan.project.models.User;
import com.toan.project.repository.UserRepository;
import com.toan.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/firebase")
public class FireBaseTestController {

    @Autowired
    FirebaseMessagingService firebaseService;
    @Autowired
    UserRepository userRepository;

    /*******************************/

    @RequestMapping("/send-notification")
    @ResponseBody
    public String sendNotification(@RequestBody Note note,
                                   @RequestParam String token) throws FirebaseMessagingException {
        return firebaseService.sendNotification(note, token);
    }

    @RequestMapping("/sendNewUploadPushMessage")
    @ResponseBody
    public String sendNotificationToTopic(@RequestBody Note note, @RequestParam String username) throws FirebaseMessagingException {
        String topicString = username.replaceAll(" ", "_");
        return firebaseService.sendNotificationToTopic(note, topicString);
    }

    @RequestMapping("/subscribe")
    @ResponseBody
    public String subscribe(@RequestParam String username) throws FirebaseMessagingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        String token = currentUser.getFirebaseToken();
        String topicString = username.replaceAll(" ", "_");
        TopicManagementResponse t = firebaseService.subscribe(token, topicString);
        String[] topicsArr;
        if (currentUser.getTopics() != null)
            topicsArr = currentUser.getTopics().split(",");
        else
            topicsArr = new String[]{};

        List<String> topics = new ArrayList<>(Arrays.asList(topicsArr));
        topics.add(topicString);
//        System.out.println(t);
        topicsArr = topics.toArray(new String[0]);
        String topicsString = String.join(",", topicsArr);
        currentUser.setTopics(topicsString);
        userRepository.save(currentUser);

        return "ok man subbed";
    }


    @RequestMapping("/unSubscribe")
    @ResponseBody
    public String unSubscribe(@RequestParam String username) throws FirebaseMessagingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        String token = currentUser.getFirebaseToken();
        String topicString = username.replaceAll(" ", "_");
        TopicManagementResponse t = firebaseService.unSubscribe(token, topicString);
        String[] topicsArr;
        if (currentUser.getTopics() != null)
            topicsArr = currentUser.getTopics().split(",");
        else
            topicsArr = new String[]{};

        List<String> topics = new ArrayList<>(Arrays.asList(topicsArr));
        topics.remove(topicString);
//        System.out.println(t);
        topicsArr = topics.toArray(new String[0]);
        String topicsString = String.join(",", topicsArr);
        currentUser.setTopics(topicsString);
        userRepository.save(currentUser);

        return "Unsubbed";
    }

    @PostMapping("/saveToken")
    public String saveFBToken(@RequestParam String firebaseToken){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        currentUser.setFirebaseToken(firebaseToken);
        userRepository.save(currentUser);
        return "Saved Token: "+ firebaseToken;
    }

    @DeleteMapping("/deleteToken")
    public String deleteFBToken(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        currentUser.setFirebaseToken(null);
        userRepository.save(currentUser);
        return "Token deleted";
    }

}

