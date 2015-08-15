package com.github.funnygopher.crowddjmobileapp.playlist;

import android.os.AsyncTask;
import android.util.Log;

import com.github.funnygopher.crowddjmobileapp.HttpRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteTask extends AsyncTask<Void, Void, Void> {

    private String playlistAddress;
    private String songURI, id, name;

    public VoteTask(String playlistAddress, String songURI, String id, String name) {
        this.playlistAddress = playlistAddress;
        this.songURI = songURI;
        this.id = id;
        this.name = name;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            HttpRequest req = new HttpRequest(HttpRequest.POST, playlistAddress);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("vote", songURI);
            parameters.put("id", id);
            parameters.put("user", name);
            req.withParameters(parameters).send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
