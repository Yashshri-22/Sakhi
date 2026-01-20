package com.example.sakhi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseProfileApi {

    @POST("/rest/v1/profiles?on_conflict=id")
    Call<Void> saveProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Header("Content-Type") String contentType,
            @Header("Prefer") String prefer,
            @Body JsonObject body
    );

    // 🔹 FETCH PROFILE
    @GET("/rest/v1/profiles")
    Call<JsonArray> getProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("id") String filter,
            @Query("select") String select
    );
}

