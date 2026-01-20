package com.example.sakhi;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    // 1. Sign Up Endpoint
    @POST("/auth/v1/signup")
    Call<JsonObject> signUp(
            @Header("apikey") String apiKey,
            @Body JsonObject body
    );

    // 2. Login Endpoint
    @POST("/auth/v1/token")
    Call<JsonObject> login(
            @Header("apikey") String apiKey,
            @Query("grant_type") String grantType,
            @Body JsonObject body
    );
}
