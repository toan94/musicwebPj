package com.toan.project.controllers;

import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.UserPayload;
import com.toan.project.repository.SongRepository;
import com.toan.project.repository.UserRepository;
import com.toan.project.security.services.UserDetailsImpl;
import com.toan.project.storageAWS.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {


    @Autowired
    UserRepository userRepository;
    @Autowired
    StorageService storageService;


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

            String defaultAva = "https://www.queensu.ca/artsci_online/sites/default/files/styles/hero_image/public" +
                    "/img/course/musc_171-mic_bokeh.jpg?itok=CkRG-0JE";

            users = pageUsers.getContent();
            List<UserPayload> uPayload = users.stream().map((u)->{

                return new UserPayload(u.getUsername(), u.getEmail(), (u.getAvatarLink() != null) ? u.getAvatarLink() :
                        defaultAva);
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

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadNewAvatar(@RequestParam MultipartFile file) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        File scratchFile = File.createTempFile("prefix", "suffix");
        try {

            Path tempPath = Paths.get(scratchFile.getAbsolutePath());
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);
            storageService.putFile("avatar/"+currentUser.getUsername()+".jpg", scratchFile);


            currentUser.setAvatarLink("https://toantestt.s3.amazonaws.com/avatar/" + currentUser.getUsername()+".jpg");
            userRepository.save(currentUser);

        } catch (Exception err) {System.err.println(err);}
        finally {
            if(scratchFile.exists()) {
                scratchFile.delete();
            }
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
