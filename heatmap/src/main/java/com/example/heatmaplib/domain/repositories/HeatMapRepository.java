package com.example.heatmaplib.domain.repositories;


import com.example.heatmaplib.data.repository.ApiResponse;

import java.io.File;
import retrofit2.Call;

public interface HeatMapRepository {

    Call<ApiResponse> uploadScreenshot(File screenshot, String simpleName);

    Call<ApiResponse> uploadPending(String map);
}
