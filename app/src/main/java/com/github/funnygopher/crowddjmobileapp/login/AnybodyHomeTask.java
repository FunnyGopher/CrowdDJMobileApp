package com.github.funnygopher.crowddjmobileapp.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketTimeoutException;

public class AnybodyHomeTask extends AsyncTask<Void, Void, Boolean> {

    private String ipAddress;

    private Context context;
    private AnybodyHomeable homeable;
    private ProgressDialog dialog;

    public AnybodyHomeTask(Context context, AnybodyHomeable homeable, String ipAddress) {
        this.context = context;
        this.homeable = homeable;
        this.ipAddress = "http://" + ipAddress + "/anybodyhome/";

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
        } else {
            homeable.somebodyIsHome();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, 1000);
        HttpConnectionParams.setSoTimeout(clientParams, 1000);

        HttpGet httpGet = new HttpGet(ipAddress);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            InputStream stream = response.getEntity().getContent();
            String responseText = readString(stream);
            Log.d("Http Get Response: ", responseText);
            return true;
        } catch (ConnectTimeoutException e) {
            Log.e("AnybodyHomeTask", "Timeout", e);
            return false;
        } catch (IOException e) {
            Log.e("LoginActivity", "AnybodyHomeTask", e);
            return false;
        }
    }

    private String readString(InputStream is) throws IOException {
        char[] buffer = new char[2048];
        Reader r = new InputStreamReader(is, "UTF-8");
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = r.read(buffer);
            if (n < 0)
                break;
            s.append(buffer, 0, n);
        }
        return s.toString();
    }
}
