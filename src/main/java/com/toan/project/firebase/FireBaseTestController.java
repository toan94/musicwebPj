package com.toan.project.firebase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.toan.project.models.PushMessage;
import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.PushMessagePayload;
import com.toan.project.payload.SongPayLoad;
import com.toan.project.repository.UserRepository;
import com.toan.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public String saveFBToken(@RequestParam String firebaseToken) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        currentUser.setFirebaseToken(firebaseToken);

        String[] topicsArr;
        if (currentUser.getTopics() != null)
            topicsArr = currentUser.getTopics().split(",");
        else
            topicsArr = new String[]{};

        List<String> topics = new ArrayList<>(Arrays.asList(topicsArr));
        topics.forEach((topic)->{
            try {
                firebaseService.subscribe(firebaseToken, topic);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
        userRepository.save(currentUser);
        return "Saved Token: "+ firebaseToken;
    }

    @DeleteMapping("/deleteToken")
    public String deleteFBToken(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        String[] topicsArr;
        if (currentUser.getTopics() != null)
            topicsArr = currentUser.getTopics().split(",");
        else
            topicsArr = new String[]{};

        List<String> topics = new ArrayList<>(Arrays.asList(topicsArr));
        String firebaseToken = currentUser.getFirebaseToken();
        topics.forEach((topic)->{
            try {
                firebaseService.unSubscribe(firebaseToken, topic);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });


        currentUser.setFirebaseToken(null);
        userRepository.save(currentUser);
        return "Token deleted";
    }


    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        String[] topicsArr;
        if (currentUser.getTopics() != null)
            topicsArr = currentUser.getTopics().split(",");
        else
            topicsArr = new String[]{};

        List<String> topics = new ArrayList<>(Arrays.asList(topicsArr));
        List<PushMessage> messages = new ArrayList<>();

        topics.forEach((topic)->{
            topic = topic.replaceAll("_", " ");
            userRepository.findByUsername(topic).ifPresent((u)->{
                messages.addAll(u.getSentMessage());
            });
        });

        Collections.sort(messages, (m1, m2) ->{
            if (m1.getCreationDate().after(m2.getCreationDate())) {
                return -1;
            } else return 1;
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        List<PushMessagePayload> mPayload = messages.stream().map((m)->{
           return new PushMessagePayload(m.getSubject(), m.getContent(), m.getUrl(), dateFormat.format(m.getCreationDate()));
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("messages", mPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
//         else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

}

