package com.toan.project.controllers;

import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.UserPayload;
import com.toan.project.repository.SongRepository;
import com.toan.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    SongRepository songRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/songs")
    public List<Song> getSongs() {

        List<Song> songs = songRepository.findAll();
        return songs;
    }
//
//    @GetMapping("/artists")
//    public List<UserPayload> getArtists() {
//
//        List<User> users = userRepository.findAll();
//        List<UserPayload> uPayload = users.stream().map((u)->{
//            return new UserPayload(u.getUsername(), u.getEmail(), "https://www.w3schools.com/w3css/img_lights.jpg");
//        }).collect(Collectors.toList());
////        Set<Song> songs = users.get(0).getSongs();
////        songs.forEach((s)->{System.out.println(s.getName());});
//        return uPayload;
//    }

    @GetMapping("/artists")
    public ResponseEntity<Map<String, Object>> getAllTutorials(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        try {
            List<User> users = new ArrayList<User>();
            Pageable paging = PageRequest.of(page, size);

            Page<User> pageUsers;
            if (title == null)
                pageUsers = userRepository.findAll(paging);
            else
                pageUsers = userRepository.findByUsernameContaining(title, paging);
//            pageUsers = userRepository.findAll(paging);

            users = pageUsers.getContent();
            List<UserPayload> uPayload = users.stream().map((u)->{
                return new UserPayload(u.getUsername(), u.getEmail(), "https://www.w3schools.com/w3css/img_lights.jpg");
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("artistList", uPayload);
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
