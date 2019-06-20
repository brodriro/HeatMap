package com.example.heatmaplib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.heatmaplib.app.HeatMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PreferencesManager {
    private static final PreferencesManager ourInstance = new PreferencesManager();

    private static final String KEY_PREFS = "HEAT_MAP";
    private static final String KEY_SESSION = "key_session";
    private static final String KEY_RAW_ACTIVITY = "raw_activity";
    private static final String TAG = PreferencesManager.class.getSimpleName();
    private static final String PHOTO_UPLOADED = "photo_uploaded";

    public static PreferencesManager getInstance() {
        return ourInstance;
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private PreferencesManager() {
        preferences = HeatMap.getApplication().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /// Set session
    public void setSession(boolean value) {
        editor.putBoolean(KEY_SESSION, value);
        editor.commit();
    }

    /// Cuando hay una sesion activa el hatmap deja de rastrear las actividades
    public boolean getSession() {
        return preferences.getBoolean(KEY_SESSION, false);
    }

    public boolean photoIsUploaded(String activityName) {
        return preferences.getBoolean(String.format("%s-%s", activityName, PHOTO_UPLOADED),false);
    }

    public void setPhotoUploaded(String activityName){
        editor.putBoolean(String.format("%s-%s", activityName, PHOTO_UPLOADED), true);
        editor.commit();
    }

    public String getRawActivity() {
        return preferences.getString(KEY_RAW_ACTIVITY, new JSONObject().toString());
    }

    public void removeRawActivity() {
        editor.putString(KEY_RAW_ACTIVITY, null);
        editor.commit();
    }

    public void setRawXY(String activityName, String rawX, String rawY) throws JSONException {
        JSONObject object = new JSONObject(getRawActivity());
        if (!object.has(activityName)) object.put(activityName, new JSONArray());

        JSONArray array = object.getJSONArray(activityName);

        JSONObject row = new JSONObject();
        row.put("x", rawX);
        row.put("y", rawY);

        array.put(row);

        editor.putString(KEY_RAW_ACTIVITY, object.toString());
        editor.commit();
    }


}
