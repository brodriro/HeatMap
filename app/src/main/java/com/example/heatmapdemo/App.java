package com.example.heatmapdemo;

import android.app.Application;

import com.example.heatmaplib.app.HeatMap;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HeatMap.init(this);
    }
}
