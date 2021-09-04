package com.toan.project.payload;

public class SongPayLoad {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public SongPayLoad(long id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    private long id;
    private String name;
    private String artist;

}
