package com.github.funnygopher.crowddjmobileapp.playlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.Song;

public class PlaylistActivity extends Activity {

    SessionManager sessionManager;
    ListView lvPlaylist;
    Button bLogout;
    PlaylistAdapter playlistAdapter;
    String playlistURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        // If the user hasn't logged in, start the login process
        if(!sessionManager.isLoggedIn()) {
            sessionManager.startSessionSetup();
            finish();
        }

        if(sessionManager.isLoggedIn()) {
            setContentView(R.layout.activity_playlist);

            playlistURL = "http://" + getIpAddress() + ":8081/playlist/";

            lvPlaylist = (ListView) findViewById(R.id.lvPlaylist);
            playlistAdapter = new PlaylistAdapter(playlistURL);
            lvPlaylist.setAdapter(playlistAdapter);
            lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Song song = playlistAdapter.getItem(position);
                    vote(song);
                    Toast.makeText(getApplicationContext(), "Voted for " + song.title + " Votes: " + String.valueOf(song.votes + 1), Toast.LENGTH_SHORT).show();
                    playlistAdapter.updatePlaylist();
                }
            });

            bLogout = (Button) findViewById(R.id.bLogout);
            bLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionManager.logoutUser();
                    finish();
                }
            });


        }
    }

    private String getIpAddress() {
        return sessionManager.getUserDetails().get(SessionManager.KEY_IP_ADDRESS);
    }

    private void vote(Song song) {
        String ip = getIpAddress();
        new VoteTask(ip, song.uri).execute();
    }
}
