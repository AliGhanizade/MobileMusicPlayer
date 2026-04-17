package com.mona.music;

import android.content.ContentUris;
import android.net.Uri;

import java.util.Locale;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String path;
    private long duration;
    private long albumId;


    public Song(long id, String title, String artist, String path, long duration, long albumId) {
        this.id = id;
        this.title = title;
        this.artist = (artist == null || artist.equals("<unknown>")) ? "Unknown Artist" : artist;
        this.path = path;
        this.duration = duration;
        this.albumId = albumId;
    }


    public Uri getAlbumArtUri() {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public String getFormattedDuration() {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    public long getAlbumId() {
        return albumId;
    }


}