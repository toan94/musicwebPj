package com.toan.project.payload;

import java.util.Date;

public class PlaylistPayload {
    private String PlaylistName;
    private String OwnedUserName;
    private Date creationDate;


    public PlaylistPayload(String playlistName, String ownedUserName, Date creationDate) {
        PlaylistName = playlistName;
        OwnedUserName = ownedUserName;
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPlaylistName() {
        return PlaylistName;
    }

    public void setPlaylistName(String playlistName) {
        PlaylistName = playlistName;
    }

    public String getOwnedUserName() {
        return OwnedUserName;
    }

    public void setOwnedUserName(String ownedUserName) {
        OwnedUserName = ownedUserName;
    }
}
