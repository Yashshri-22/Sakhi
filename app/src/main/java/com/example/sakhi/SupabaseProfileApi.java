package com.example.sakhi;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseProfileApi {

    @POST("/rest/v1/profiles")
    Call<Void> saveProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Header("Content-Type") String contentType,
            @Header("Prefer") String prefer,
            @Body JsonObject body
    );
}
