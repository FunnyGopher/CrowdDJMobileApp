package com.github.funnygopher.crowddjmobileapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Kyle on 6/18/2015.
 */
public class HTTPGetTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            client.execute(new HttpGet(params[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
