package com.github.funnygopher.crowddjmobileapp.playlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.Song;

public class PlaylistActivity extends AppCompatActivity {

    public static final int SORT_MODE_TITLE = 0;
    public static final int SORT_MODE_ARTIST = 1;

    private SessionManager sessionManager;

    private Toolbar toolbar;
    private Spinner spinner;

    private ListView listview_playlist;
    private Button btn_leave;
    private PlaylistAdapter playlistAdapter;
    private String playlistURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        sessionManager = new SessionManager(getApplicationContext());
        playlistURL = "http://" + getIpAddress() + "/playlist/";

        // The toolbar at the top
        toolbar = (Toolbar) findViewById(R.id.toolbar_playlist);
        toolbar.setTitle("Playlist");
        setSupportActionBar(toolbar);

        // The sort spinner in the toolbar
        spinner = (Spinner) findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.spinner_sort, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                playlistAdapter.setSortMode(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // The listview that holds the playlist
        listview_playlist = (ListView) findViewById(R.id.listview_playlist);
        playlistAdapter = new PlaylistAdapter(playlistURL);
        listview_playlist.setAdapter(playlistAdapter);
        listview_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = sessionManager.getSessionPreferences().get(SessionManager.KEY_ID);
                String name = sessionManager.getUserPreferences().get(SessionManager.KEY_NAME);
                Song song = playlistAdapter.getItem(position);
                song.vote(playlistURL, userId, name);

                //Toast.makeText(getApplicationContext(), "Voted for " + song.title + " Votes: " + String.valueOf(song.votes + 1), Toast.LENGTH_SHORT).show();
                playlistAdapter.fetchPlaylist();
            }
        });

        // The leave button
        btn_leave = (Button) findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.destroySession();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getIpAddress() {
        return sessionManager.getSessionPreferences().get(SessionManager.KEY_ADDRESS);
    }

    private void vote(Song song) {
        String id = sessionManager.getSessionPreferences().get(SessionManager.KEY_ID);
        String name = sessionManager.getUserPreferences().get(SessionManager.KEY_NAME);

        VoteTask task = new VoteTask(playlistURL, song.uri, id, name);
        task.execute();
    }
}
