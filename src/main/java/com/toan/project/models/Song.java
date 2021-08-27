package com.toan.project.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(	name = "songs")
public class Song {

    public Song() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name="name")
    private String name;

    @ManyToMany(mappedBy = "Songs")
    private Set<Playlist> currentlyInPlayListSet;
//    @NotBlank
//    @Size(max = 50)
//    @Column(name = "genre")
//    private String genre;

//    @NotBlank
//    @Size(max = 100)
//    @Column(name="description")
//    private String description;



    @ManyToOne
    @JoinColumn(name="user_id")
    @NotNull
    private User artist;

    public Set<Playlist> getCurrentlyInPlayListSet() {
        return currentlyInPlayListSet;
    }

    public void setCurrentlyInPlayListSet(Set<Playlist> currentlyInPlayListSet) {
        this.currentlyInPlayListSet = currentlyInPlayListSet;
    }

    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getGenre() {
//        return genre;
//    }
//
//    public void setGenre(String genre) {
//        this.genre = genre;
//    }

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

}
