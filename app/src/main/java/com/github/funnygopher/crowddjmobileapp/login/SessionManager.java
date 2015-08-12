package com.github.funnygopher.crowddjmobileapp.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.funnygopher.crowddjmobileapp.login.LoginActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    private static final String PREFS_NAME = "CrowdDJ Preferences";
    private static final String IS_LOGIN = "IsLoggedIn";

    // The user data keys
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SERVER_IP_ADDRESS = "ip address";

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Adds all of the user data to the apps preferences
    public void createLoginSession(String id, String name, String ipAddress) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_SERVER_IP_ADDRESS, ipAddress);
        editor.commit();
    }

    // Return information about the user
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_SERVER_IP_ADDRESS, pref.getString(KEY_SERVER_IP_ADDRESS, null));
        return user;
    }

    // Brings the user to the login activity
    public void startSessionSetup() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Removes the user's data from the preferences
    public void logoutUser() {
        String name = pref.getString(KEY_NAME, "");
        String ipAddress = pref.getString(KEY_SERVER_IP_ADDRESS, "");

        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("ip address", ipAddress);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
