package com.example.hyunwook.schedulermacbooktroops.holiday;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 18-09-18
 * Retrofit request
 */
public class RequestHoliday {

    private static RequestHoliday ourInstance = new RequestHoliday();


    public static RequestHoliday getInstance() {
        return ourInstance;
    }

    private RequestHoliday() {
       /* HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient okhttp = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        new Retrofit.Build().client(okhttp);*/
    }

    //http://schedule.mactroops.com/v1/holidays?year=2018
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://schedule.mactroops.com/v1/holidays?year=")
            .addConverterFactory(GsonConverterFactory.create())
            //.addConverterFactory(RxJavaCallA
            .build();

    RetrofitInterface service = retrofit.create(RetrofitInterface.class);

    /**
     * http://schedule.mactroops.com:3000/v1/holiday/2018
     */
    public RetrofitInterface getService() {
        return service;
    }
}
