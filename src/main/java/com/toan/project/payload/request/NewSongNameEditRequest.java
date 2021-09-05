package com.toan.project.payload.request;

public class NewSongNameEditRequest {
    private Long songId;
    private String newSongName;

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getNewSongName() {
        return newSongName;
    }

    public void setNewSongName(String newSongName) {
        this.newSongName = newSongName;
    }

    public NewSongNameEditRequest(Long songId, String newSongName) {
        this.songId = songId;
        this.newSongName = newSongName;
    }
}
