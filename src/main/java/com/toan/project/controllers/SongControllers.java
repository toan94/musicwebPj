package com.toan.project.controllers;

import com.toan.project.models.Playlist;
import com.toan.project.models.PushMessage;
import com.toan.project.models.Song;
import com.toan.project.models.User;
import com.toan.project.payload.SongPayLoad;
import com.toan.project.payload.request.ActionOnPlaylistRequestPayload;
import com.toan.project.payload.request.NewSongNameEditRequest;
import com.toan.project.payload.request.SongDeleteRequestPayload;
import com.toan.project.repository.PlaylistRepository;
import com.toan.project.repository.PushMessageRepository;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/songs")
public class SongControllers {

    @Autowired
    SongRepository songRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    PushMessageRepository pushMessageRepository;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSongs(
            @RequestParam(required = false) String genre,
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        try {
            List<Song> songs = new ArrayList<Song>();
            Pageable paging = PageRequest.of(page, size);

            Page<Song> pageSongs;
            if (title == null && genre == null)
                pageSongs = songRepository.findAll(paging);
            else if (title == null && genre != null)
                pageSongs = songRepository.findByGenreContaining(genre, paging);
            else if (title != null && genre == null)
                pageSongs = songRepository.findByNameContaining(title, paging);
            else
                pageSongs = songRepository.findByGenreAndNameContaining(genre, title, paging);
//            pageUsers = userRepository.findAll(paging);

            songs = pageSongs.getContent();
//            AtomicBoolean isPurchased = new AtomicBoolean(false);
            List<SongPayLoad> sPayload = songs.stream().map((s)->{
//                boolean isPurchased = currentUser.getUser_id() == s.getBuyer().getUser_id();
                SongPayLoad sp = new SongPayLoad(s.getId(), s.getName(), s.getArtist().getUsername(), s.isForSale(),
                        false, s.getGenre());
                s.getBuyers().forEach((buyer)->{
                    if (buyer.getUser_id() == currentUser.getUser_id()) {
//                        isPurchased.set(true);
                        sp.setPurchased(true);
                    }
                });
                return sp;
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

    @GetMapping("/belongedPlaylists")
    public ResponseEntity<Map<String, Object>> getBelongedPlaylists(@RequestParam Long songId ) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isPresent()) {
            Set<Playlist> currentlyInPlayListSet = song.get().getCurrentlyInPlayListSet();
            Set<Long> currentlyInPlaylistsID =
                    currentlyInPlayListSet.stream().map((p)->p.getId()).collect(Collectors.toSet());
            Map<String, Object> response = new HashMap<>();
            response.put("belongedPlaylists", currentlyInPlaylistsID);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addToPlaylist")
    public ResponseEntity<?> addToPlaylist(@RequestBody ActionOnPlaylistRequestPayload addRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User currentUser = userRepository.findById(userDetails.getId()).get();

            Set<Playlist> ownedPlaylists = currentUser.getListOfPlaylist();
            Set<Long> ownedPlaylistsId = ownedPlaylists.stream().map(pl-> pl.getId()).collect(Collectors.toSet());

            Optional<Playlist> playlist = playlistRepository.findById(addRequest.getPlaylistId());
            Optional<Song> song = songRepository.findById(addRequest.getSongId());
            if (playlist.isPresent() && song.isPresent()) {
                Playlist p = playlist.get();
                Song s = song.get();
                if (ownedPlaylistsId.contains(p.getId())){

                    p.getSongs().add(s);
                    playlistRepository.save(p);
                    return  ResponseEntity.ok(" song id:" + addRequest.getSongId() + " added to playlist id: " + addRequest.getPlaylistId());
                } else  return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

            } else  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//            currentUser.getListOfPlaylist();
//            Playlist p = new Playlist();
//            p.setName(newPlaylist.getName());
//            p.setOwnedUser(currentUser);
//            playlistRepository.save(p);
        }else{
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/removeFromPlaylist")
    public ResponseEntity<?> removeFromPlaylist(@RequestBody ActionOnPlaylistRequestPayload addRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User currentUser = userRepository.findById(userDetails.getId()).get();

            Set<Playlist> ownedPlaylists = currentUser.getListOfPlaylist();
            Set<Long> ownedPlaylistsId = ownedPlaylists.stream().map(pl-> pl.getId()).collect(Collectors.toSet());

            Optional<Playlist> playlist = playlistRepository.findById(addRequest.getPlaylistId());
            Optional<Song> song = songRepository.findById(addRequest.getSongId());
            if (playlist.isPresent() && song.isPresent()) {
                Playlist p = playlist.get();
                Song s = song.get();
                if (ownedPlaylistsId.contains(p.getId())){
                    if (p.getSongs().contains(s)) {
                        p.getSongs().remove(s);
                        playlistRepository.save(p);
                        return  ResponseEntity.ok(" song id:" + addRequest.getSongId() + " removed from playlist id: " + addRequest.getPlaylistId());
                    } else  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else  return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

            } else  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//            currentUser.getListOfPlaylist();
//            Playlist p = new Playlist();
//            p.setName(newPlaylist.getName());
//            p.setOwnedUser(currentUser);
//            playlistRepository.save(p);
        }else{
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping("/songsFromPlaylist")
    public ResponseEntity<Map<String, Object>> getAllSongsFromPlaylist(@RequestParam Long playlistId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        if (playlist.isPresent()) {
            Playlist pl = playlist.get();

            List<SongPayLoad> sPayload = pl.getSongs().stream().map((s)->{
                 SongPayLoad sp = new SongPayLoad(s.getId(), s.getName(), s.getArtist().getUsername(), s.isForSale(),
                        false, s.getGenre());
                s.getBuyers().forEach((buyer)->{
                    if (buyer.getUser_id() == currentUser.getUser_id()) {
                        sp.setPurchased(true);
                    }
                });
                return sp;
            }).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("songList", sPayload);
            response.put("playlistName", pl.getName());
            response.put("creationDate", pl.getCreationDate());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/mySongs")
    public ResponseEntity<Map<String, Object>> getMySongs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        Set<Song> songs = currentUser.getSongs();

        Set<SongPayLoad> sPayload = songs.stream().map((s)->{
            SongPayLoad sp = new SongPayLoad(s.getId(),s.getName(),s.getArtist().getUsername(), s.isForSale(), false,
                    s.getGenre());
            s.getBuyers().forEach((buyer)->{
                if (buyer.getUser_id() == currentUser.getUser_id()) {
                    sp.setPurchased(true);
                }
            });
            return sp;
        }).collect(Collectors.toSet());

        Map<String, Object> response = new HashMap<>();
        response.put("songList", sPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PatchMapping("/editSong")
    public ResponseEntity<?> editSong(@RequestBody NewSongNameEditRequest editInfo) {
        Optional<Song> Song = songRepository.findById(editInfo.getSongId());
        if (Song.isPresent()) {
            Song s = Song.get();

            storageService.renameFile(s.getName()+".mp3", editInfo.getNewSongName()+".mp3");

            s.setName(editInfo.getNewSongName());
            songRepository.save(s);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadNewSong(@RequestParam MultipartFile file, @RequestParam String songName,
                                           @RequestParam boolean forSale, @RequestParam String genre) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        File scratchFile = File.createTempFile("prefix", "suffix");
        try {
//            Path path1 = Paths.get("C:/Users/Admin/Desktop", "gg2.mp3");
//            Files.copy(file.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
            Path tempPath = Paths.get(scratchFile.getAbsolutePath());
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);
            storageService.putFile(songName+".mp3", scratchFile);

            Song newSong = new Song();
            newSong.setName(songName);
            newSong.setArtist(currentUser);
            newSong.setForSale(forSale);
            newSong.setGenre(genre);
            Set<User> buyers = new HashSet<>();
            buyers.add(currentUser);
            newSong.setBuyers(buyers);
            songRepository.save(newSong);

            String subject = currentUser.getUsername() + " uploaded a new song";
            String content = "Song name: " + songName;
            String url = "/artist/" +currentUser.getUsername();


            PushMessage pushMessage = new PushMessage(subject, content, url);
            pushMessage.setSender(currentUser);
            pushMessageRepository.save(pushMessage);

        } catch (Exception err) {System.err.println(err);}
        finally {
            if(scratchFile.exists()) {
                scratchFile.delete();
            }
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/songsOfUser")
    public ResponseEntity<Map<String, Object>> getSongsByUsername(@RequestParam String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {

            User u = user.get();
            Set<Song> songs = u.getSongs();
            Set<SongPayLoad> sPayload = songs.stream().map((s)->{
                SongPayLoad sp =  new SongPayLoad(s.getId(), s.getName(), s.getArtist().getUsername(), s.isForSale(),
                        false, s.getGenre());
                s.getBuyers().forEach((buyer)->{
                    if (buyer.getUser_id() == currentUser.getUser_id()) {
                        sp.setPurchased(true);
                    }
                });
                return sp;
            }).collect(Collectors.toSet());

            Map<String, Object> response = new HashMap<>();
            response.put("songList", sPayload);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/deleteSong")
    public ResponseEntity<?> deleteSong(@RequestBody SongDeleteRequestPayload deleteRequestPayload) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
//        User currentUser = userRepository.findById(userDetails.getId()).get();
//
//        Set<Song> songs = currentUser.getSongs();
//        Set<Song> updatedSongList = songs.stream().map((s)->{
//            if (s.getId() != deleteRequestPayload.getSongId())
//                return s;
//            else return null;
//        }).collect(Collectors.toSet());
//        currentUser.setSongs(updatedSongList);
//        userRepository.save(currentUser);
        songRepository.deleteById(deleteRequestPayload.getSongId());

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
