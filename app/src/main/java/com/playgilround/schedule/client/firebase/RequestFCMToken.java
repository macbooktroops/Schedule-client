package com.playgilround.schedule.client.firebase;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 18-09-30
 * FCM Request
 */
public class RequestFCMToken {

    static final String TAG = RequestFCMToken.class.getSimpleName();
    private static RequestFCMToken ourInstance = new RequestFCMToken();
    public static RequestFCMToken getInstance() {
        return ourInstance;
    }

    private RequestFCMToken() {

    }
  /*  OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
//                    ongoing.addHeader("Accept", "application/json;versions=1");
                        ongoing.addHeader("Authorization", "eJrf0_A2Qxs:APA91bG17pliGqdtRikIYE7RJyjZFp0HDnMfOzSWLD-Oi-20E4nrW1LZzwLv2gNF_3IelUAwMUYnoJzmZpGmGeXrAIlg7VgUz-Uwq7rmDnWkSk-rUlHV1R1l4NqMcD7oPh4HT9TAh1AV");
                    return chain.proceed(ongoing.build());
                }
            })
            .build();*/
    //fcm_token
    Retrofit retrofit = new Retrofit.Builder()
//            .client(httpClient)
            .baseUrl("http://schedule.mactroops.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

 /*   OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Log.d(TAG, "Token intercept ------>" + chain + "--" + resToken);

            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", resToken)
                    .build();

            return chain.proceed(request);
        }
    }).build();*/
    RetrofitFirebase service = retrofit.create(RetrofitFirebase.class);

    public RetrofitFirebase getService() {
        return service;
    }
}
