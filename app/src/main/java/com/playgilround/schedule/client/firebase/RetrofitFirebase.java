package com.playgilround.schedule.client.firebase;

import android.util.Log;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 18-09-30
 * Firebase Retrofit header, POST
 */
public interface RetrofitFirebase {

    @Headers({"Accept: application/json", "Content-Type: application/json", "Authorization: "})
    @POST("v1/fcm_token")
//    Call<TokenSerialized> postToken(@Header("Authorization") JsonObject token);
    Call<TokenSerialized> postToken(@Body JsonObject token, @Header("Authorization") String tokenData);
}
