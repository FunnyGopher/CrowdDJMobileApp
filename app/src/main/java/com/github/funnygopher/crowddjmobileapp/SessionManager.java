package com.github.funnygopher.crowddjmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.funnygopher.crowddjmobileapp.activity.LoginActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    private static final String PREFS_NAME = "CrowdDJ Preferences";
    private static final String IS_LOGIN = "IsLoggedIn";

    // The user data keys
    public static final String KEY_NAME = "name";
    public static final String KEY_IP_ADDRESS = "ip address";
    public static final String KEY_PHONE_NUMBER = "phone number";

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Adds all of the user data to the apps preferences
    public void createLoginSession(String name, String ipAddress, String phoneNumber) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_IP_ADDRESS, ipAddress);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.commit();
    }

    // Return information about the user
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_IP_ADDRESS, pref.getString(KEY_IP_ADDRESS, null));
        user.put(KEY_PHONE_NUMBER, pref.getString(KEY_PHONE_NUMBER, null));
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
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
