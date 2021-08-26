package com.toan.project.payload;

public class SongPayLoad {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SongPayLoad(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String name;
    private String artist;
}
