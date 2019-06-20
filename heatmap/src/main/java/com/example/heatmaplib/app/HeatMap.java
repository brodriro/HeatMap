package com.example.heatmaplib.app;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.heatmaplib.utils.PreferencesManager;

public class HeatMap implements Application.ActivityLifecycleCallbacks, View.OnTouchListener, HeatMapPresenter.HeatMapView {

    private static final String TAG = HeatMap.class.getSimpleName();

    private static Application application;
    private Activity activity;
    private Display display;

    private HeatMapPresenter presenter;

    public static void init(Application application) {
        new HeatMap(application);
    }

    private HeatMap(Application application) {
        setApplication(application);
        presenter = new HeatMapPresenter(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        display = activity.getWindowManager().getDefaultDisplay();
        presenter.setDisplay(display);
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        this.activity = activity;

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        activity.getWindow().getDecorView().getRootView().setOnTouchListener(this);
        showChildren(viewGroup);

    }

    @Override
    public void onActivityResumed(Activity activity) {
        boolean photoIsUploaded = PreferencesManager.getInstance().photoIsUploaded(activity.getClass().getSimpleName());

        if (!photoIsUploaded && isStoragePermissionGranted(activity)) {
            new Handler().postDelayed(() -> presenter.takeScreenshot(activity), 900);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (activity == null) return false;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            presenter.setRawXY(activity.getClass().getSimpleName(), event);
            Log.e(TAG, String.format("Display:Width:%s  Height:%s", display.getWidth(), display.getHeight()));
        }

        return false;
    }

    private void showChildren(View v) {
        ViewGroup viewgroup = (ViewGroup) v;
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            v1.setOnTouchListener(this);
            if (v1 instanceof ViewGroup) showChildren(v1);
        }
    }

    public static Application getApplication() {
        return application;
    }

    private static void setApplication(Application application) {
        HeatMap.application = application;
    }

    @Override
    public void startMonitoring() {
        Log.e("HEATMAP", "Enabled listeners");
        application.registerActivityLifecycleCallbacks(this);
    }

    public boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }
}
