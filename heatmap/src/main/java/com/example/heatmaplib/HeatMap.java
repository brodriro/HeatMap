package com.example.heatmaplib;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;

public class HeatMap implements Application.ActivityLifecycleCallbacks, View.OnTouchListener {

    private static final String TAG = HeatMap.class.getSimpleName();

    private Application application;
    private Activity activity;
    private Display display;

    public static void init(Application application) {
        new HeatMap(application);
    }

    private HeatMap(Application application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }


    private void monitoringActivity(Activity activity, Display display, View view, MotionEvent event) {
        Log.e(TAG, "monitoringActivity:" + activity.getClass().getSimpleName());

        float x = event.getRawX();
        float y = event.getRawY();

        Log.e(TAG, String.format("Display:Width:%s  Height:%s", display.getWidth(), display.getHeight()));
        Log.e(TAG, String.format("X:%s  Y:%s", getPercent(display.getWidth(), x), getPercent(display.getHeight(), y)));
    }

    private static String getPercent(int dimension, float value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format((value / dimension) * 100);
    }

    private Bitmap takeScreenShot(Activity activity) {
        View v = activity.getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);

        // creates immutable clone
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    private void showChildren(View v) {
        ViewGroup viewgroup = (ViewGroup) v;
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            v1.setOnTouchListener(this);
            if (v1 instanceof ViewGroup) showChildren(v1);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        Log.e(TAG, "onActivityStarted : " + activity.getClass().getSimpleName());

        this.activity = activity;
        display = activity.getWindowManager().getDefaultDisplay();

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        showChildren(viewGroup);

        activity.getWindow().getDecorView().getRootView().setOnTouchListener(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e(TAG, "onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e(TAG, "onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e(TAG, "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e(TAG, "onActivityDestroyed");
        this.activity = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (activity== null) return false;

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
            monitoringActivity(activity, display, v, event);
        }

        return false;
    }



}
