package com.toan.project.models;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(	name = "pushMessage")
@Transactional
public class PushMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    @NotBlank
    private String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;
//    @PrePersist
//    protected void onCreate() {
//        creationDate = new Date();
//    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    public PushMessage() {
    }

    public PushMessage(String subject, String content, String url) {
        this.subject = subject;
        this.content = content;
        this.url = url;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}