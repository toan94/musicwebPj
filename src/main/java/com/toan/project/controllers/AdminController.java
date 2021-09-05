package com.toan.project.controllers;

import com.toan.project.models.Playlist;
import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.request.SongDeleteRequestPayload;
import com.toan.project.payload.request.UserDeleteRequestPayload;
import com.toan.project.repository.PlaylistRepository;
import com.toan.project.repository.SongRepository;
import com.toan.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SongRepository songRepository;
    @Autowired
    PlaylistRepository playlistRepository;


    @PostMapping("/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSong(@RequestBody UserDeleteRequestPayload deleteRequestPayload) {

        Optional<User> user = userRepository.findByUsername(deleteRequestPayload.getName());
        if (user.isPresent()) {
            Set<Playlist> playlists= user.get().getListOfPlaylist();
            Set<Song> songs= user.get().getSongs();

            playlists.forEach((pl)->{
                pl.setSongs(null);
                playlistRepository.save(pl);
            });

            songs.forEach((s)->{
                s.getCurrentlyInPlayListSet().forEach((pl)->{
                    pl.getSongs().remove(s);
                    playlistRepository.save(pl);
                });
                songRepository.delete(s);
            });
//            User u = user.get();
//            u.setSongs(null);
//            userRepository.save(u);
        }

        userRepository.deleteByUsername(deleteRequestPayload.getName());

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
