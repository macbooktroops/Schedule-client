package com.playgilround.calendar.widget.calendar.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    //공휴일
    @Headers("Content-Type: application/json")
    @GET(BaseUrl.PATH_HOLIDAYS)
    Call<ArrayList<JsonObject>> getListHoliday(@Query(BaseUrl.PARAM_YEAR) int year);

    //회원가입
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST(BaseUrl.PATH_SIGN_UP)
    Call<Result> postSignUp(@Body JsonObject login);

    //로그인
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST(BaseUrl.PATH_SIGN_IN)
    Call<JsonObject> postSignIn(@Body JsonObject login);

    //토큰
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_FCM_TOKEN)
    Call<TokenSerialized> postToken(@Body JsonObject token, @Header("Authorization") String tokenData);

    //유저검색
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_USER_SEARCH)
    Call<JsonObject> postUserSearch(@Body JsonObject user, @Header("Authorization") String tokenData);

    //친구추가
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_NEW_FRIEND)
    Call<JsonArray> postNewFriend(@Body JsonArray friend, @Header("Authorization") String tokenData);

    //내친구목록 검색
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET(BaseUrl.PATH_FRIEND_SEARCH)
    Call<JsonArray> getFriendSearch(@Header("Authorization") String tokenData);

    //스케줄 추가
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_NEW_SCHEDULE)
    Call<JsonObject> postNewSchedule(@Body JsonObject addSche, @Header("Authorization") String tokenData);

}
