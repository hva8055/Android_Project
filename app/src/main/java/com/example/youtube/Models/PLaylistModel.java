package com.example.youtube.Models;

public class PLaylistModel {
    String playlist_name;
    String uid;
    int videos;

    public PLaylistModel(String uid, int videos, String playlist_name) {
        this.uid = uid;
        this.videos = videos;
        this.playlist_name = playlist_name;
    }

    public PLaylistModel(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }
}
