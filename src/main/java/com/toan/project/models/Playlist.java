package com.toan.project.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(	name = "playlist")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PLAYLIST_ID")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name="name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "PLAYLIST_SONG",
            joinColumns = @JoinColumn(name = "PLAYLIST_ID"),
            inverseJoinColumns = @JoinColumn(name = "SONG_ID")
    )
    private Set<Song> Songs;

    public Set<Song> getSongs() {
        return Songs;
    }

    public void setSongs(Set<Song> songs) {
        Songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
