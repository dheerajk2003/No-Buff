package com.dheerakk2003.MariaMaven.models;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "UPLOAD")
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String filename;

    private String title;

    @Column(name = "image")
    private String image;

    public Upload(){}

    public Upload(Long userId, String filename) {
        this.userId = userId;
        this.filename = filename;
    }

    public Upload(Long userId, String filename, String title, String image) {
        this.userId = userId;
        this.filename = filename;
        this.title = title;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
