package com.example.common.holiday;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 18-09-19
 */
public interface RetrofitInterface {

    @Headers("Content-Type: application/json")
    @GET("/v1/holidays?")
    Call<ArrayList<JsonObject>> getListHoliday(@Query("year") int year);

//    @GET("{year}")
//    io.reactivex.Observable<JsonObject> getListHoliday(@Path("year") int year);
//    Observable<ArrayList<JsonObject>> getListHoliday(@Path("year") int year);

//    @GET("{year}


}
