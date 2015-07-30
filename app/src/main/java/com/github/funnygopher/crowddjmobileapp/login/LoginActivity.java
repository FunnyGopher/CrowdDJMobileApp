package com.github.funnygopher.crowddjmobileapp.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.playlist.PlaylistActivity;

public class LoginActivity extends Activity implements AnybodyHomeable {

    SessionManager sessionManager;
    EditText txtName, txtIpAddress;
    Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        txtName = (EditText) findViewById(R.id.txtName);
        txtIpAddress = (EditText) findViewById(R.id.txtIpAddress);
        bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String ipAddress = txtIpAddress.getText().toString();

                if(name.trim().length() <= 0) {
                    showMessage("No Name", "Why don't you have a name?");
                    return;
                }

                if(ipAddress.trim().length() <= 0) {
                    showMessage("No IP Address", "Not a valid IP Address!");
                    return;
                }

                checkIfAnybodyIsHome(ipAddress);
            }
        });
    }

    public void showMessage(String title, String message) {
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

    public void checkIfAnybodyIsHome(String ipAddress) {
        AnybodyHomeTask anybodyTask = new AnybodyHomeTask(LoginActivity.this, this, ipAddress);
        anybodyTask.execute();
    }

    public void nobodyIsHome() {
        showMessage("Nobody Was Home", "There wasn't a server at that IP Address!");
    }

    public void somebodyIsHome() {
        // Get the phone number of the phone the app is running on. This acts as the UUID.
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();

        String name = txtName.getText().toString();
        String ipAddress = txtIpAddress.getText().toString();
        sessionManager.createLoginSession(name, ipAddress, phoneNumber);

        Intent intent = new Intent(LoginActivity.this, PlaylistActivity.class);
        startActivity(intent);
        finish();
    }
}
