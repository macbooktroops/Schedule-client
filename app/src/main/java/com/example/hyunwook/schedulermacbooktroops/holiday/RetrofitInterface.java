package com.example.hyunwook.schedulermacbooktroops.holiday;

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
}
