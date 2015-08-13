package com.github.funnygopher.crowddjmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.funnygopher.crowddjmobileapp.join.JoinActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences userPref, sessionPref;
    SharedPreferences.Editor userEditor, sessionEditor;
    Context context;
    int PRIVATE_MODE = 0;

    private static final String USER_PREF_NAME = "User Preferences";
    private static final String SESSION_PREF_NAME = "Session Preferences";
    private static final String IN_SESSION = "InSession";

    // The data keys
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";

    public SessionManager(Context context) {
        this.context = context;
        userPref = this.context.getSharedPreferences(USER_PREF_NAME, PRIVATE_MODE);
        sessionPref = this.context.getSharedPreferences(SESSION_PREF_NAME, PRIVATE_MODE);

        userEditor = userPref.edit();
        sessionEditor = sessionPref.edit();
    }

    public void saveUserPreferences(String name) {
        userEditor.putString(KEY_NAME, name);
        userEditor.commit();
    }

    public HashMap<String, String> getUserPreferences() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, userPref.getString(KEY_NAME, null));
        return user;
    }

    public void join() {
        Intent intent = new Intent(context, JoinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Adds all of the user data to the apps preferences
    public void createSession(String id, String address) {
        sessionEditor.putBoolean(IN_SESSION, true);
        sessionEditor.putString(KEY_ID, id);
        sessionEditor.putString(KEY_ADDRESS, address);
        sessionEditor.commit();
    }

    // Return information about the user
    public HashMap<String, String> getSessionPreferences() {
        HashMap<String, String> session = new HashMap<>();
        session.put(KEY_ID, sessionPref.getString(KEY_ID, null));
        session.put(KEY_ADDRESS, sessionPref.getString(KEY_ADDRESS, null));
        return session;
    }

    // Removes the user's data from the preferences
    public void destroySession() {
        String address = sessionPref.getString(KEY_ADDRESS, "");

        sessionEditor.clear();
        sessionEditor.commit();

        Intent intent = new Intent(context, JoinActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean inSession() {
        return sessionPref.getBoolean(IN_SESSION, false);
    }
}
