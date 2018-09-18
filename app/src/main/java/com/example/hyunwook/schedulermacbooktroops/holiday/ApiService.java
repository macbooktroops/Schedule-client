package com.example.hyunwook.schedulermacbooktroops.holiday;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * 18-09-18
 * API Method 방식, Path정의
 * http://schedule.mactroops.com:3000/v1/holiday/2018
 */
public interface ApiService {

    @GET("v1/holiday/{path}")
    Call holidayInfo(@Path("path") String path);
}
