package com.github.funnygopher.crowddjmobileapp.playlist;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class VoteTask  extends AsyncTask<Void, Void, Void> {

    private String voteAddress;
    private String id;
    private String user;
    private String songURI;

    public VoteTask(String ipAddress, String id, String user, String songURI) {
        voteAddress = "http://" + ipAddress + "/playlist/";
        this.id = id;
        this.user = user;
        this.songURI = songURI;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(voteAddress);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
        parameters.add(new BasicNameValuePair("vote", songURI));
        parameters.add(new BasicNameValuePair("id", id));
        parameters.add(new BasicNameValuePair("user", user));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse response = httpClient.execute(httpPost);
            Log.d("Http Post Response: ", response.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
