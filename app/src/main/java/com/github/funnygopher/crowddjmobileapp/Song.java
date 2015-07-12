package com.github.funnygopher.crowddjmobileapp;

public class Song {
    public final String title;
    public final String artist;
    public final String uri;
    public final int votes;

    public Song(String title, String artist, String uri, int votes) {
        this.title = title;
        this.artist = artist;
        this.uri = uri;
        this.votes = votes;
    }
}
