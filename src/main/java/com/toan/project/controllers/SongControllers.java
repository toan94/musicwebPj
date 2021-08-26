package com.toan.project.controllers;

import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.SongPayLoad;
import com.toan.project.payload.UserPayload;
import com.toan.project.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/songs")
public class SongControllers {

    @Autowired
    SongRepository songRepository;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllTutorials(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

//        List<Song> songs = songRepository.findAll();
//        List<SongPayLoad> sPayLoad = songs.stream().map( s-> {
//            return new SongPayLoad(s.getName(), s.getArtist().getUsername());
//        }).collect(Collectors.toList());
//
//        return sPayLoad;
        try {
            List<Song> songs = new ArrayList<Song>();
            Pageable paging = PageRequest.of(page, size);

            Page<Song> pageSongs;
            if (title == null)
                pageSongs = songRepository.findAll(paging);
            else
                pageSongs = songRepository.findByNameContaining(title, paging);
//            pageUsers = userRepository.findAll(paging);

            songs = pageSongs.getContent();
            List<SongPayLoad> sPayload = songs.stream().map((s)->{
                return new SongPayLoad(s.getName(), s.getArtist().getUsername());
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("songList", sPayload);
            response.put("currentPage", pageSongs.getNumber());
            response.put("totalItems", pageSongs.getTotalElements());
            response.put("totalPages", pageSongs.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
