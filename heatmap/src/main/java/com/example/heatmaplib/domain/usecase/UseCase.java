package com.example.heatmaplib.domain.usecase;

import com.example.heatmaplib.data.repository.ApiResponse;
import com.example.heatmaplib.domain.repositories.HeatMapRepository;

import java.io.File;

import retrofit2.Call;

public class UseCase {

    private HeatMapRepository repository;

    public UseCase(HeatMapRepository repository) {
        this.repository = repository;
    }

    public Call<ApiResponse> uploadScreenshot(File file, String simpleName) {
        return repository.uploadScreenshot(file, simpleName);
    }

    public Call<ApiResponse> uploadPending(String map) {
        return repository.uploadPending(map);
    }

}
