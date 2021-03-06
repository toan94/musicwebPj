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

    public boolean isForSale() {
        return isForSale;
    }

    public void setForSale(boolean forSale) {
        isForSale = forSale;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public SongPayLoad(long id, String name, String artist, boolean isForSale, boolean isPurchased, String genre) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.isForSale = isForSale;
        this.isPurchased = isPurchased;
        this.genre = genre;
    }

    private long id;
    private String name;
    private String artist;
    private boolean isForSale;
    private boolean isPurchased;
    private String genre;

}
