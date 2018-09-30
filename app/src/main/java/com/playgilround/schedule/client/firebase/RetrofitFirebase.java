package com.playgilround.schedule.client.firebase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 18-09-30
 * Firebase Retrofit header, POST
 */
public interface RetrofitFirebase {

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("v1/fcm_token")
    Call<String> postToken(@Body String token);
}
