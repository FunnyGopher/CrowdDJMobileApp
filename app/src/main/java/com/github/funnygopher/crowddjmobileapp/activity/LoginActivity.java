package com.github.funnygopher.crowddjmobileapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;

public class LoginActivity extends Activity {

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

                if(name.trim().length() > 0 && ipAddress.trim().length() > 0) {
                    // Get the phone number of the phone the app is running on. This acts as the UUID.
                    TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String phoneNumber = tMgr.getLine1Number();

                    sessionManager.createLoginSession(name, ipAddress, phoneNumber);

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
