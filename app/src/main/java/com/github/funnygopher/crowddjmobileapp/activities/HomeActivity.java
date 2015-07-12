package com.github.funnygopher.crowddjmobileapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.funnygopher.crowddjmobileapp.R;
import com.github.funnygopher.crowddjmobileapp.SessionManager;
import com.github.funnygopher.crowddjmobileapp.tasks.HTTPGetTask;


public class HomeActivity extends Activity {

    SessionManager sessionManager;
    Button bPlay, bPause, bStop, bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        // If the user hasn't logged in, start the login process
        if(!sessionManager.isLoggedIn()) {
            sessionManager.startSessionSetup();
            finish();
        }

        setContentView(R.layout.activity_home);

        bPlay = (Button) findViewById(R.id.bPlay);
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        bPause = (Button) findViewById(R.id.bPause);
        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        bStop = (Button) findViewById(R.id.bStop);
        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        bLogout = (Button) findViewById(R.id.logout);
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
                finish();
            }
        });
    }

    private String getIpAddress() {
        return sessionManager.getUserDetails().get(SessionManager.KEY_IP_ADDRESS);
    }

    private void play() {
        String ip = getIpAddress();
        new HTTPGetTask().execute("http://" + ip + ":8081/playback/?command=play");
    }

    private void pause() {
        String ip = getIpAddress();
        new HTTPGetTask().execute("http://" + ip + ":8081/playback/?command=pause");
    }

    private void stop() {
        String ip = getIpAddress();
        new HTTPGetTask().execute("http://" + ip + ":8081/playback/?command=stop");
    }
}
