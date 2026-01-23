package com.example.sakhi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabasePeriodApi {

    @GET("/rest/v1/menstrual_data")
    Call<JsonArray> getCycle(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("user_id") String userId
    );

    @POST("/rest/v1/menstrual_data")
    Call<Void> saveCycle(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Header("Prefer") String prefer,
            @Body JsonObject body
    );
}
