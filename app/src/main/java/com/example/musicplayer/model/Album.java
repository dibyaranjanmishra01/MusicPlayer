package com.example.musicplayer.model;

public class Album {

    long id;
    String title;
    String albumArtist;
    String AlbumartPath;
    String tracks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbumartPath() {
        return AlbumartPath;
    }

    public void setAlbumartPath(String albumartPath) {
        AlbumartPath = albumartPath;
    }

    public String getTracks() {
        return tracks;
    }

    public void setTracks(String tracks) {
        this.tracks = tracks;
    }
}
