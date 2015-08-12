package com.github.funnygopher.crowddjmobileapp.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.playlist.PlaylistActivity;

public class LoginActivity extends AppCompatActivity implements AnybodyHomeable {

    SessionManager sessionManager;
    EditText input_name, input_address;
    AppCompatButton btn_join;

    private String ipAddress;
    private boolean withQRCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        input_name = (EditText) findViewById(R.id.input_name);
        input_address = (EditText) findViewById(R.id.input_address);
        btn_join = (AppCompatButton) findViewById(R.id.btn_join);

        Intent intent = getIntent();
        if(intent.getDataString() != null) {
            String dataString = intent.getDataString();
            ipAddress = dataString.replace("crowddjmobileapp://", "");
            ((ViewManager)input_address.getParent()).removeView(input_address);
            withQRCode = true;
        }

        Bundle extras = intent.getExtras();
        if(extras != null && !extras.isEmpty()) {
            String name = extras.getString("name", "");
            String ipAddress = extras.getString("ip_address", "");
            input_name.setText(name);
            input_address.setText(ipAddress);
        }

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input_name.getText().toString();

                if(!withQRCode) {
                    ipAddress = input_address.getText().toString();
                }

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (!wifi.isConnected()) {
                    showMessage("No Wifi", "You must be on the same wifi network as the music player!");
                    return;
                }

                if (name.trim().length() <= 0) {
                    showMessage("Missing Name", "Why don't you have a name?");
                    return;
                }

                if (ipAddress.trim().length() <= 0) {
                    showMessage("Missing Player Address", "You have to tell me what server to connect to!");
                    return;
                }

                checkIfAnybodyIsHome(ipAddress);
            }
        });
    }

    private String unhashServerCode(String hashedIp, String hashedPort) {
        String unhashedIp = String.valueOf(Long.valueOf(hashedIp, 36));
        String octets = unhashedIp.substring(0, unhashedIp.length() - 4);
        String lengthData = unhashedIp.substring(unhashedIp.length() - 4, unhashedIp.length());

        String[] baseAddress = getBaseAddress().split("\\.");
        int totalLength = 0;
        for(int i = 3; i >= 0; i--) {
            int length = Integer.valueOf(Character.toString(lengthData.charAt(i)));
            if(length == 0)
                continue;

            totalLength += length;
            String octet = octets.substring(octets.length() - totalLength, octets.length() - totalLength + length);
            baseAddress[i] = octet;
        }

        String address = "";
        for(int i = 0; i < 4; i++) {
            address += baseAddress[i];
            if(i < 3) {
                address += ".";
            }
        }
        int port = Integer.valueOf(hashedPort, 36);

        String ipAddress = address + ":" + port;
        return ipAddress;
    }

    public void checkIfAnybodyIsHome(String ipAddress) {
        AnybodyHomeTask anybodyTask = new AnybodyHomeTask(LoginActivity.this, this, ipAddress);
        anybodyTask.execute();
    }

    public void nobodyIsHome() {
        showMessage("Can't Find Player", "I couldn't hear anything playing...");
    }

    public void somebodyIsHome() {
        // Get the phone number of the phone the app is running on. This acts as the UUID.
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        String name = input_name.getText().toString();
        sessionManager.createLoginSession(macAddress, name, ipAddress);

        Intent intent = new Intent(LoginActivity.this, PlaylistActivity.class);
        startActivity(intent);
        finish();
    }

    private String getBaseAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        int newNumber = dhcpInfo.ipAddress & dhcpInfo.netmask;

        String something = (newNumber & 0xFF) + "." +
                ((newNumber >> 8 ) & 0xFF) + "." +
                ((newNumber >> 16 ) & 0xFF) + "." +
                ((newNumber >> 24 ) & 0xFF) ;
        Log.i("LocateServerTask", something);

        return something;
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
