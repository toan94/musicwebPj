package com.toan.project.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name="user_id")
    @NotNull
    private User OwnedUser;

    @ManyToMany
    @JoinTable(
            name = "PLAYLIST_SONG",
            joinColumns = @JoinColumn(name = "PLAYLIST_ID"),
            inverseJoinColumns = @JoinColumn(name = "SONG_ID")
    )
    private Set<Song> Songs;

    @Temporal(TemporalType.DATE)
    Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Song> getSongs() {
        return Songs;
    }
    public void setSongs(Set<Song> songs) {
        Songs = songs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwnedUser() {
        return OwnedUser;
    }

    public void setOwnedUser(User ownedUser) {
        OwnedUser = ownedUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
