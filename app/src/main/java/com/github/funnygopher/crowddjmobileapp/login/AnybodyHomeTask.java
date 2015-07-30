package com.github.funnygopher.crowddjmobileapp.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AnybodyHomeTask extends AsyncTask<Void, Void, Boolean> {

    private String ipAddress;

    private Context context;
    private AnybodyHomeable homeable;
    private ProgressDialog dialog;

    public AnybodyHomeTask(Context context, AnybodyHomeable homeable, String ipAddress) {
        this.context = context;
        this.homeable = homeable;
        this.ipAddress = "http://" + ipAddress + ":8081/anybodyhome/";

        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Checking IP address...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }

        if(!success) {
            homeable.nobodyIsHome();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(ipAddress);

        try {
            HttpResponse response = httpClient.execute(httpGet);

            InputStream stream = response.getEntity().getContent();
            String responseText = readString(stream);
            Log.d("Http Get Response: ", responseText);

            // If response is "I'm Here!" return true
            if(responseText.equals("I'm Home!")) {
                homeable.somebodyIsHome();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.e("LoginActivity", "AnybodyHomeTask", e);
            return false;
        }
    }

    private String readString(InputStream is) throws IOException {
        char[] buf = new char[2048];
        Reader r = new InputStreamReader(is, "UTF-8");
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = r.read(buf);
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }
        return s.toString();
    }
}
