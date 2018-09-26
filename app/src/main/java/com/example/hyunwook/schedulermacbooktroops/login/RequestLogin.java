package com.example.hyunwook.schedulermacbooktroops.login;

import com.example.hyunwook.schedulermacbooktroops.holiday.RetrofitInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 18-09-25 Login SignIn
 */
public class RequestLogin {

    private static RequestLogin ourInstance = new RequestLogin();
    public static RequestLogin getInstance() {
        return ourInstance;
    }

    private RequestLogin() {

    }

    /**
     * http://localhost:3000/users/sign_in
     * Email, password
     *
     * rails s -b 0.0.0.0로 로컬 테스트 시
     * http://192.168.219.104:3000/ 내 맥북 아이피.
     */
    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://localhost:3000/")
            .baseUrl("http://192.168.219.104:3000/")
//            .baseUrl("http://schedule.mactroops.com/")

            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitLogin service = retrofit.create(RetrofitLogin.class);

    /**
     * http://localhost:3000/users/sign_in
     */
    public RetrofitLogin getService() {
        return service;
    }
}
