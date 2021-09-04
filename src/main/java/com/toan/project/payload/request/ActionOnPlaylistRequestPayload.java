package com.toan.project.payload.request;

public class ActionOnPlaylistRequestPayload {
    private long playlistId;
    private long songId;

    public ActionOnPlaylistRequestPayload(long playlistId, long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
