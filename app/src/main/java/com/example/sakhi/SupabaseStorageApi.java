package com.example.sakhi;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseStorageApi {

    @Multipart
    @POST("/storage/v1/object/profile-images/{fileName}")
    Call<Void> uploadImage(
            @Header("Authorization") String auth,
            @Path("fileName") String fileName,
            @Query("upsert") String upsert,
            @Part MultipartBody.Part file
    );
}
