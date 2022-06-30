package com.example.musicplayer.model;

/*
View Model for item tiles of Song Recycler View
 */

import android.graphics.Color;

public class Song {
    private String title;
    private String duration;
    private String artist;
    private String album_id;
    private String path;
    private int dominantColor;

    public Song() {
        this.title = "";
        this.duration = "";
        this.artist = "";
        dominantColor = Color.BLACK;
    }

    public int getDominantColor() {
        return dominantColor;
    }

    public void setDominantColor(int dominantColor) {
        this.dominantColor = dominantColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
