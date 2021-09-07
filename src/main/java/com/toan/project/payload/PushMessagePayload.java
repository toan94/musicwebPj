package com.toan.project.payload;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class PushMessagePayload {

//    private Long id;
    private String subject;
    private String content;
    private String url;
    private String creationDate;

    public PushMessagePayload() {
    }

    public PushMessagePayload(String subject, String content, String url, String creationDate) {
        this.subject = subject;
        this.content = content;
        this.url = url;
        this.creationDate = creationDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
