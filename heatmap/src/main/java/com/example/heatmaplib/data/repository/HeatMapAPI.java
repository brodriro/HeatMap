package com.example.heatmaplib.data.repository;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HeatMapAPI {

    String BASE_URL = "http://192.168.1.135:3000/";

    @Multipart
    @POST("api/upload-screenshot")
    Call<ApiResponse> uploadScreenShot (@Part MultipartBody.Part photo);


    @FormUrlEncoded
    @POST("api/upload-pending")
    Call<ApiResponse> uploadPending(@Field("map") String map);

}
