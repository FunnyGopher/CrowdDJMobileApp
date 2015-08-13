package com.github.funnygopher.crowddjmobileapp.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.funnygopher.crowddjmobileapp.HttpRequest;

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

    private String anybodyHomeAddress;

    private Context context;
    private AnybodyHomeable homeable;
    private ProgressDialog dialog;

    public AnybodyHomeTask(Context context, AnybodyHomeable homeable, String ipAddress) {
        this.context = context;
        this.homeable = homeable;
        this.anybodyHomeAddress = "http://" + ipAddress + "/anybodyhome/";

        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Listening for bumpin' music...");
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
        try {
            HttpRequest req = new HttpRequest(HttpRequest.GET, anybodyHomeAddress);
            String response = req.sendAndGetResponse();
            Log.d("Http Get Response: ", response);
            return true;
        } catch (IOException e) {
            Log.e("LoginActivity", "AnybodyHomeTask", e);
            return false;
        }
    }
}
