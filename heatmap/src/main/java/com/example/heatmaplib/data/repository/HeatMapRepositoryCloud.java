package com.example.heatmaplib.data.repository;

import android.util.Log;

import com.example.heatmaplib.domain.repositories.HeatMapRepository;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class HeatMapRepositoryCloud extends RetrofitRepository implements HeatMapRepository {

    @Override
    public Call<ApiResponse> uploadScreenshot(File screenshot, String simpleName) {
        HeatMapAPI api = getRetrofit(HeatMapAPI.BASE_URL).create(HeatMapAPI.class);

        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), screenshot);
        MultipartBody.Part part = MultipartBody.Part.createFormData("screenshot", String.format("%s.jpg", simpleName), body);
        return api.uploadScreenShot(part);
    }

    @Override
    public Call<ApiResponse> uploadPending(String map) {
        Log.e("Cloud", "uploadPending");
        HeatMapAPI api = getRetrofit(HeatMapAPI.BASE_URL).create(HeatMapAPI.class);

        return api.uploadPending(map);
    }
}
