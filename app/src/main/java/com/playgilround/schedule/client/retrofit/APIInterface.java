package com.playgilround.schedule.client.retrofit;

import com.google.gson.JsonObject;
import com.playgilround.schedule.client.login.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    //공휴일
    @Headers("Content-Type: application/json")
    @GET(BaseUrl.PATH_HOLIDAYS)
    Call<ArrayList<JsonObject>> getListHoliday(@Query(BaseUrl.PARAM_YEAR) int year);

    //회원가입
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST(BaseUrl.PARAM_SIGNUP)
    Call<Result> postSignUp(@Body JsonObject login);

    //로그인
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST(BaseUrl.PARAM_SIGNIN)
    Call<JsonObject> postSignIn(@Body JsonObject login);
}
