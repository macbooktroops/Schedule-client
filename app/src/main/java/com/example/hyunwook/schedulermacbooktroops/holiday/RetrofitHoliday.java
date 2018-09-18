package com.example.hyunwook.schedulermacbooktroops.holiday;

import com.example.hyunwook.schedulermacbooktroops.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 18-09-18
 * Retrofit
 */
public class RetrofitHoliday {

    private static RetrofitHoliday retInstance = new RetrofitHoliday();
    //singleton
    public static RetrofitHoliday getInstance() {
        return retInstance;
    }

    public RetrofitHoliday() {

    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://schedule.mactroops.com:3000/v1/holiday/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    HolidayService service = retrofit.create(HolidayService.class);

    public HolidayService getService() {
        return service;
    }
}
