package com.github.funnygopher.crowddjmobileapp.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;

public class LocateServerTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private CanLocateServer locateable;
    private ProgressDialog dialog;
    private String correctIP;
    private boolean cancelled;

    public LocateServerTask(Context context, CanLocateServer locateable) {
        this.context = context;
        this.locateable = locateable;
        dialog = new ProgressDialog(context);

        correctIP = "";
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Finding available music players...");
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        int baseAddress = getBaseAddress();
        Log.i("LocateServerTask", String.valueOf(baseAddress));

        HttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, 100);
        HttpConnectionParams.setSoTimeout(clientParams, 100);

        int range = 0;
        while(!cancelled && range < 256) {
            String ip = intToIp(baseAddress + (int)(Math.pow(2, 24) * range));
            range += 1;
            Log.i("LocateServerTask", ip);

            String ipAddress = "http://" + ip + ":8081/anybodyhome/";
            HttpGet httpGet = new HttpGet(ipAddress);

            try {
                HttpResponse response = httpClient.execute(httpGet);
                InputStream stream = response.getEntity().getContent();
                String responseText = readString(stream);
                Log.d("Http Get Response: ", responseText);

                if(responseText.equals("I'm Home!")) {
                    correctIP = ip;
                    return true;
                } else {
                    continue;
                }
            } catch (ConnectTimeoutException e) {
                //Log.e("AnybodyHomeTask", "Timeout", e);
                continue;
            } catch (IOException e) {
                continue;
            }
        }

        return false;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }

        locateable.serverLocated(success, correctIP);
    }

    @Override
    protected void onCancelled() {
        cancelled = true;
    }

    private int getBaseAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        int newNumber = dhcpInfo.ipAddress & dhcpInfo.netmask;

        String something = (newNumber & 0xFF) + "." +
                ((newNumber >> 8 ) & 0xFF) + "." +
                ((newNumber >> 16 ) & 0xFF) + "." +
                ((newNumber >> 24 ) & 0xFF) ;
        Log.i("LocateServerTask", something);

        return newNumber;
    }

    private String intToIp(int address) {
        return (address & 0xFF) + "." +
                ((address >> 8 ) & 0xFF) + "." +
                ((address >> 16 ) & 0xFF) + "." +
                ((address >> 24 ) & 0xFF) ;
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
