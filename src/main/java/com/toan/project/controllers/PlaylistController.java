package com.toan.project.controllers;

import com.toan.project.models.Playlist;
import com.toan.project.models.Song;
import com.toan.project.payload.PlaylistPayload;
import com.toan.project.payload.SongPayLoad;
import com.toan.project.repository.PlaylistRepository;
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
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    PlaylistRepository playlistRepository;


    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPlaylists(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        String a = "gg";
        try {
            List<Playlist> playlists = new ArrayList<>();
            Pageable paging = PageRequest.of(page, size);

            Page<Playlist> pagePlaylists;
//            if (title == null)
//                pagePlaylists = playlistRepository.findAll(paging);
//            else
//                pagePlaylists = playlistRepository.findByNameContaining(title, paging);
            pagePlaylists = playlistRepository.findAll(paging);

            playlists = pagePlaylists.getContent();
            List<PlaylistPayload> pPayload = playlists.stream().map((p)->{
                return new PlaylistPayload(p.getName(), p.getOwnedUser().getUsername());
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("listOfPlaylist", pPayload);
            response.put("currentPage", pagePlaylists.getNumber());
            response.put("totalItems", pagePlaylists.getTotalElements());
            response.put("totalPages", pagePlaylists.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
