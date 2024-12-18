package com.example.youtube.Models;

public class VideoModel {
    private String title;
    private String url;
    private String description;

    public VideoModel() {
    }

    public VideoModel(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}

