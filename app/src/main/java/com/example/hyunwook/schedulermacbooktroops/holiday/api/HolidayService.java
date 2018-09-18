package com.example.hyunwook.schedulermacbooktroops.holiday.api;

import com.example.hyunwook.schedulermacbooktroops.holiday.model.Holiday;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * 18-09-18
 * API Method 방식, Path정의
 * http://schedule.mactroops.com:3000/v1/holiday/2018
 */
public interface HolidayService {

//    @GET("{year}")
    @GET(HolidayUrl.HOLI_YEAR)
//    Call<ArrayList<JsonObject>> getHolidayInfo(@Path("year") String year);
    Call<Holiday> requestHoliInfo(@Path(HolidayUrl.HOLI_PATH) int year);
}
