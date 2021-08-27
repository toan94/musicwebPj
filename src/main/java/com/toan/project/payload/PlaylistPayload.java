package com.toan.project.payload;

public class PlaylistPayload {
    private String PlaylistName;
    private String OwnedUserName;


    public PlaylistPayload(String playlistName, String ownedUserName) {
        PlaylistName = playlistName;
        OwnedUserName = ownedUserName;
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
