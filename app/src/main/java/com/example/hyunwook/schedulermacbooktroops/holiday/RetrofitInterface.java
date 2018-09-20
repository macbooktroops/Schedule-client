package com.example.hyunwook.schedulermacbooktroops.holiday;

import android.database.Observable;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * 18-09-19
 */
public interface RetrofitInterface {

    @GET("{year}")
    Call<ArrayList<JsonObject>> getListHoliday(@Path("year") int year);

//    @GET("{year}")
//    io.reactivex.Observable<JsonObject> getListHoliday(@Path("year") int year);
//    Observable<ArrayList<JsonObject>> getListHoliday(@Path("year") int year);


}
