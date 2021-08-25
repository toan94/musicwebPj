package com.toan.project.payload;

public class UserPayload {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
    public UserPayload(String name, String email, String coverImg) {
        this.email = email;
        this.name = name;
        this.coverImg = coverImg;
    }
    private String name;
    private String email;
    private String coverImg;
}
