package com.toan.project.payload.request;

import javax.validation.constraints.NotBlank;

public class NewPlaylistRequest {

    @NotBlank
    private String name;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
