package com.github.funnygopher.crowddjmobileapp.playlist;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.funnygopher.crowddjmobileapp.HttpRequest;
import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.Song;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends BaseAdapter {

    public final String PLAYLIST_URL;
    List<Song> playlist = new ArrayList<Song>();

    public PlaylistAdapter(String playlistURL) {
        this.PLAYLIST_URL = playlistURL;
        updatePlaylist();

    }

    @Override
    public int getCount() {
        return playlist.size();
    }

    @Override
    public Song getItem(int position) {
        return playlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_playlist, parent, false);
        }

        ImageView coverArt = (ImageView) convertView.findViewById(R.id.img_cover_art);
        TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView artist = (TextView) convertView.findViewById(R.id.txtArtist);
        TextView votes = (TextView) convertView.findViewById(R.id.txtVotes);

        Song song = playlist.get(position);
        title.setText(song.title);
        artist.setText(song.artist);
        votes.setText(song.votes + " votes");

        return convertView;
    }

    private List<Song> getTestSongs() {
        List<Song> playlist = new ArrayList<Song>();

        playlist.add(new Song("Let It Be (feat. Veela)", "Blackmill", "D%3A%5CUsers%5CKyle%5CMusic%5CBlackmill%5CBlackmill%20-%20Miracle%5C03%20Let%20It%20Be%20%28feat.%20Veela%29.mp3", 1));
        playlist.add(new Song("Embrace", "Blackmill", "D%3A%5CUsers%5CKyle%5CMusic%5CBlackmill%5CBlackmill%20-%20Miracle%5C04%20Embrace.mp3", 0));
        playlist.add(new Song("Do I Wanna Know?", "Artic Monkeys", "D%3A%5CUsers%5CKyle%5CMusic%5C%28Singles%29%5CDo%20I%20Wanna%20Know.mp3", 3));
        return playlist;
    }

    public void updatePlaylist() {
        new DownloadXmlTask(this).execute(PLAYLIST_URL);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<Song>> {

        PlaylistAdapter adapter;

        public DownloadXmlTask(PlaylistAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected List<Song> doInBackground(String... urls) {
            try {
                return getSongsFromXML(urls[0]);
            } catch (IOException e) {
                Log.e("CrowdDJ", "Uh-oh", e);
                return new ArrayList<Song>();
            } catch (XmlPullParserException e) {
                Log.e("CrowdDJ", "Uh-oh", e);
                return new ArrayList<Song>();
            }
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            playlist = songs;
            adapter.notifyDataSetChanged();
        }
    }

    // Uploads XML and parses it. Returns a
    private List<Song> getSongsFromXML(String urlString) throws IOException, XmlPullParserException {
        InputStream stream = null;

        // Instantiate the parser
        PlaylistXMLParser parser = new PlaylistXMLParser();
        List<Song> playlist = null;

        try {
            stream = downloadUrl(urlString);
            playlist = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return playlist;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        try {
            HttpRequest req = new HttpRequest(HttpRequest.GET, urlString);
            String response = req.sendAndGetResponse();
            return new ByteArrayInputStream(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
