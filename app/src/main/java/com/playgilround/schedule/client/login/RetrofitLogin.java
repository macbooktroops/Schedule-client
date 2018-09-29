package com.playgilround.schedule.client.login;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * rails s --> 서버 시작
 */
public interface RetrofitLogin {

    /*@POST("signin")
    Call<ArrayList<JsonObject>> postSignIn(@Query("email") String email, @Query("password") String password);*/

    @Headers({"Content-Type: application/json", "Accept: application/json"})
//    @Headers("Accept: applecation/json")
    @POST("users/sign_in")
    Call<JsonObject> postSignIn(@Body JsonObject login);
//    Call<ArrayList<JsonObject>> postSignIn(@Field("email") String email, @Field("encrypted_password") String password);

//    JsonLogin postSignIn(@Body)

}
