package com.playgilround.schedule.client.retrofit;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface APIInterface {

    @Headers("Content-Type: application/json")
    @GET(BaseUrl.PATH_HOLIDAYS)
    Call<ArrayList<JsonObject>> getListHoliday(@Query("year") int year);
}
