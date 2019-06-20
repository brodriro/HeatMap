package com.example.heatmaplib.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.example.heatmaplib.data.repository.HeatMapRepositoryCloud;
import com.example.heatmaplib.data.repository.ApiResponse;
import com.example.heatmaplib.domain.usecase.UseCase;
import com.example.heatmaplib.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeatMapPresenter {
    interface HeatMapView {
        void startMonitoring();
    }

    private HeatMapView heatMapView;
    private Display display;
    private UseCase useCase;

    HeatMapPresenter(HeatMapView heatMapView) {
        this.heatMapView = heatMapView;
        this.useCase = new UseCase(new HeatMapRepositoryCloud());
        uploadPending();

    }

    void uploadPending() {
        PreferencesManager preferences = PreferencesManager.getInstance();
        String map = preferences.getRawActivity();

        if (map.equals(new JSONObject().toString())) {
            heatMapView.startMonitoring();
        }else {
            useCase.uploadPending(map).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.body()!= null && response.body().getStatus().equals("OK")){
                        preferences.removeRawActivity();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });
        }
    }

    void setDisplay(Display display) {
        this.display = display;
    }

    void setRawXY(String activityName, MotionEvent event) {

        String x = getPercent(display.getWidth(), event.getRawX());
        String y = getPercent(display.getHeight(), event.getRawY());

        try {
            PreferencesManager.getInstance().setRawXY(activityName, x, y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void takeScreenshot(Activity activity) {

        try {
            File file = _takeScreenshot(activity);
            if (file == null) return;

            useCase.uploadScreenshot(file, activity.getClass().getSimpleName()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.body() != null && response.body().getStatus().equals("OK")) {
                        PreferencesManager.getInstance().setPhotoUploaded(activity.getClass().getSimpleName());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPercent(int dimension, float value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format((value / dimension) * 100);
    }

    private File _takeScreenshot(Activity activity) {
        File imageFile = null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(display.getWidth(), display.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            activity.getWindow().getDecorView().getRootView().draw(canvas);

            File outputDir = activity.getCacheDir(); // context being the Activity pointer
            imageFile = File.createTempFile(activity.getClass().getSimpleName(), ".jpg", outputDir);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return imageFile;
    }
}
