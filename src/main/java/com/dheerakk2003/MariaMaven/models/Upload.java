package com.dheerakk2003.MariaMaven.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String filename;

    private String title;

    public Upload(){}

    public Upload(Long userId, String filename) {
        this.userId = userId;
        this.filename = filename;
    }

    public Upload(Long userId, String filename, String title) {
        this.userId = userId;
        this.filename = filename;
        this.title = title;
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
}
