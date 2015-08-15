package com.github.funnygopher.crowddjmobileapp;

import com.github.funnygopher.crowddjmobileapp.playlist.VoteTask;

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

    public void vote(String address, String id, String name) {
        VoteTask task = new VoteTask(address, uri, id, name);
        task.execute();
    }
}
