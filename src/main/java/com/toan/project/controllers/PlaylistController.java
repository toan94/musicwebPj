package com.toan.project.controllers;

import com.toan.project.models.Playlist;
import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.PlaylistPayload;
import com.toan.project.payload.SongPayLoad;
import com.toan.project.payload.request.NewPlaylistRequest;
import com.toan.project.repository.PlaylistRepository;
import com.toan.project.repository.SongRepository;
import com.toan.project.repository.UserRepository;
import com.toan.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPlaylists(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "false") boolean noPaging
    ) {
//        try {
//            if(noPaging) {
//                List<Playlist> pl = playlistRepository.findAll();
//                List<PlaylistPayload> pPayload = pl.stream().map((p)->{
//                    return new PlaylistPayload(p.getId(), p.getName(), p.getOwnedUser().getUsername(), p.getCreationDate());
//                }).collect(Collectors.toList());
//                Map<String, Object> response = new HashMap<>();
//                response.put("listOfPlaylist", pPayload);
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
//            List<Playlist> playlists = new ArrayList<>();
//            Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
//
//            Page<Playlist> pagePlaylists;
//            if (title == null)
//                pagePlaylists = playlistRepository.findAll(paging);
//            else
//                pagePlaylists = playlistRepository.findByNameContaining(title, paging);
////            pagePlaylists = playlistRepository.findAll(paging);
//
//            playlists = pagePlaylists.getContent();
//            List<PlaylistPayload> pPayload = playlists.stream().map((p)->{
//                return new PlaylistPayload(p.getId(), p.getName(), p.getOwnedUser().getUsername(), p.getCreationDate());
//            }).collect(Collectors.toList());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("listOfPlaylist", pPayload);
//            response.put("currentPage", pagePlaylists.getNumber());
//            response.put("totalItems", pagePlaylists.getTotalElements());
//            response.put("totalPages", pagePlaylists.getTotalPages());
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User currentUser = userRepository.findById(userDetails.getId()).get();
            Set<Playlist> playlists = currentUser.getListOfPlaylist();
            List<PlaylistPayload> pPayload = playlists.stream().map((p)->{
                return new PlaylistPayload(p.getId(), p.getName(), p.getOwnedUser().getUsername(), p.getCreationDate());
            }).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("listOfPlaylist", pPayload);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }else{
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewPlaylist(@Valid @RequestBody NewPlaylistRequest newPlaylist){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User currentUser = userRepository.findById(userDetails.getId()).get();
            Playlist p = new Playlist();
            p.setName(newPlaylist.getName());
            p.setOwnedUser(currentUser);
            playlistRepository.save(p);
            return  ResponseEntity.ok("new playlist created");
        }else{
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}
