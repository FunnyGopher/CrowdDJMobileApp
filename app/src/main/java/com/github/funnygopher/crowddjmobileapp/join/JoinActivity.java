package com.github.funnygopher.crowddjmobileapp.join;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.playlist.PlaylistActivity;

public class JoinActivity extends AppCompatActivity implements AnybodyHomeable {

    EditText input_name, input_address;
    AppCompatButton btn_join;
    SessionManager sessionManager;

    private String address;
    private boolean withQRCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.inSession()) {
            joinNoValidation();
        }

        setContentView(R.layout.activity_join);
        input_name = (EditText) findViewById(R.id.input_name);
        input_address = (EditText) findViewById(R.id.input_address);
        btn_join = (AppCompatButton) findViewById(R.id.btn_join);


        // Handles the name field
        input_name.setText(sessionManager.getUserPreferences().get(SessionManager.KEY_NAME));
        if(!input_name.getText().toString().isEmpty()) {
            input_address.requestFocus();
        }

        // If the app was opened via QRCode...
        Intent intent = getIntent();
        if(intent.getDataString() != null) {
            String dataString = intent.getDataString();
            address = dataString.replace("crowddjmobileapp://", "");
            ((ViewManager)input_address.getParent()).removeView(input_address);
            withQRCode = true;
        }

        // If the user had a previously saved address
        Bundle extras = intent.getExtras();
        if(extras != null && !extras.isEmpty()) {
            String address = extras.getString(SessionManager.KEY_ADDRESS, "");
            input_address.setText(address);
        }

        input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sessionManager.saveUserPreferences(editable.toString());
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join();
            }
        });
    }

    private void join() {
        if(!validate()) {
            return;
        }

        String address = withQRCode ? this.address : input_address.getText().toString();
        checkIfAnybodyIsHome(address);
    }

    private void joinNoValidation() {
        String address = sessionManager.getSessionPreferences().get(SessionManager.KEY_ADDRESS);
        checkIfAnybodyIsHome(address);
    }

    private boolean validate() {
        boolean valid = true;

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!wifi.isConnected()) {
            showMessage("No Wifi", "You must be on the same wifi network as the music player!");
            return false;
        }

        String name = input_name.getText().toString();
        if(name.isEmpty()) {
            input_name.setError("Why don't you have a name?");
            valid = false;
        } else {
            input_name.setError(null);
        }

        String address = withQRCode ? this.address : input_address.getText().toString();
        if(address.isEmpty() || address.length() < 9) {
            input_address.setError("You have to tell me what server to connect to!");
            valid = false;
        } else {
            input_address.setError(null);
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SessionManager.KEY_NAME, input_name.getText().toString());
        outState.putString(SessionManager.KEY_ADDRESS, input_address.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        input_name.setText(savedInstanceState.getString(SessionManager.KEY_NAME));
        input_address.setText(savedInstanceState.getString(SessionManager.KEY_ADDRESS));
    }

    public void checkIfAnybodyIsHome(String address) {
        AnybodyHomeTask anybodyTask = new AnybodyHomeTask(JoinActivity.this, this, address);
        anybodyTask.execute();
    }

    @Override
    public void nobodyIsHome() {
        showMessage("Can't Find Player", "I couldn't hear anything playing...");
    }

    @Override
    public void somebodyIsHome() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        String address = input_address.getText().toString();
        sessionManager.createSession(macAddress, address);

        Intent intent = new Intent(JoinActivity.this, PlaylistActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this)
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
}
