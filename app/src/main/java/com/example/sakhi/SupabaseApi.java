package com.example.sakhi;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("/auth/v1/recover")
    Call<Void> resetPassword(
            @Header("apikey") String apiKey,
            @Body JsonObject body
    );

    @PUT("/auth/v1/user")
    Call<Void> updatePassword(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Header("Content-Type") String contentType,
            @Body JsonObject body
    );

}
