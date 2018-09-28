package com.example.hyunwook.schedulermacbooktroops.login;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 18-09-27
 * Register
 */
public interface RetrofitRegister {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("users")
    Call<Result> postSignUp(@Body JsonObject login);
}
