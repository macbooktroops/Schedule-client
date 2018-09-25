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
     */
    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://localhost:3000/users/")
//            .baseUrl("http://172.16.6.221010:3000/")
            .baseUrl("http://172.16.6.1:3000/")
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
