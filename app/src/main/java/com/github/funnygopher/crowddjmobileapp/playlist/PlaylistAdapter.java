package com.github.funnygopher.crowddjmobileapp.playlist;

import android.content.Context;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaylistAdapter extends BaseAdapter {

    private final String PLAYLIST_URL;
    private List<Song> playlist = new ArrayList<Song>();
    private int sortMode;

    public PlaylistAdapter(String playlistURL) {
        this.PLAYLIST_URL = playlistURL;
        sortMode = 0;
        fetchPlaylist();
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

    public void fetchPlaylist() {
        new GetPlaylistTask(this, sortMode).execute();
        sort(sortMode);
    }

    public void sort(int sortMode) {
        if(sortMode == PlaylistActivity.SORT_MODE_TITLE) {
            Collections.sort(playlist, new Comparator<Song>() {
                @Override
                public int compare(Song song1, Song song2) {
                    return song1.title.compareTo(song2.title);
                }
            });
        }

        if(sortMode == PlaylistActivity.SORT_MODE_ARTIST) {
            Collections.sort(playlist, new Comparator<Song>() {
                @Override
                public int compare(Song song1, Song song2) {
                    if(song1.artist.equals("")) {
                        return 1;
                    }
                    return song1.artist.compareTo(song2.artist);
                }
            });
        }
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
        sort(sortMode);
        notifyDataSetChanged();
    }


    public class GetPlaylistTask extends AsyncTask<Void, Void, List<Song>>{

        private PlaylistAdapter adapter;
        private int sortMode;

        public GetPlaylistTask(PlaylistAdapter adapter, int sortMode) {
            this.adapter = adapter;
            this.sortMode = sortMode;
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            try {
                return getPlaylist(PLAYLIST_URL);
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
            adapter.sort(this.sortMode);
            adapter.notifyDataSetChanged();
        }

        // Uploads XML and parses it. Returns a
        private List<Song> getPlaylist(String address) throws IOException, XmlPullParserException {
            InputStream stream = null;

            // Instantiate the parser
            PlaylistXMLParser parser = new PlaylistXMLParser();
            List<Song> playlist = null;

            try {
                stream = getXMLStream(address);
                playlist = parser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return playlist;
        }

        private InputStream getXMLStream(String address) {
            try {
                HttpRequest req = new HttpRequest(HttpRequest.GET, address);
                String response = req.sendAndGetResponse();
                return new ByteArrayInputStream(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
