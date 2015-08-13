package com.github.funnygopher.crowddjmobileapp.playlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.Song;

public class PlaylistActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ListView lvPlaylist;
    Button bLogout;
    PlaylistAdapter playlistAdapter;
    String playlistURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        lvPlaylist = (ListView) findViewById(R.id.lvPlaylist);

        sessionManager = new SessionManager(getApplicationContext());

        playlistURL = "http://" + getIpAddress() + "/playlist/";

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
                sessionManager.destroySession();
                finish();
            }
        });
    }

    private String getIpAddress() {
        return sessionManager.getSessionPreferences().get(SessionManager.KEY_ADDRESS);
    }

    private void vote(Song song) {
        String id = sessionManager.getSessionPreferences().get(SessionManager.KEY_ID);
        String name = sessionManager.getSessionPreferences().get(SessionManager.KEY_NAME);

        VoteTask task = new VoteTask(playlistURL, song.uri, id, name);
        task.execute();
    }
}
