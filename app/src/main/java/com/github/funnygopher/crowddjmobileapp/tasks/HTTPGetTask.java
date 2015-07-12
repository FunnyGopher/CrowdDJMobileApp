package com.github.funnygopher.crowddjmobileapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class HTTPGetTask extends AsyncTask<String, Void, Void> {

    InputStream inputStream;

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
