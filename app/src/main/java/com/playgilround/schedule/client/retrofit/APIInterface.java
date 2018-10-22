package com.playgilround.schedule.client.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.playgilround.schedule.client.gson.Result;
import com.playgilround.schedule.client.gson.TokenSerialized;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    //이메일 찾기
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_FIND_EMAIL)
    Call<JsonObject> postFindEmail(@Body JsonObject findEmail);

    //1.패스워드 토큰 얻기
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_FIND_PASSWORD)
    Call<JsonObject> postFindPassWord(@Body JsonObject findPassword);

    //패스워드 리셋
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_RESET_PASSWORD)
    Call<JsonObject> postResetPassword(@Body JsonObject resetPw, @Header("Authorization") String tokenData);

    //친구 동의
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_FRIEND_ASSENT)
    Call<JsonObject> postFriendAssent(@Body JsonObject friendAssent, @Path("pushId") int pushid, @Header("Authorization") String tokenData);

    //스케줄 업데이트
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_UPDATE_SCHEDULE)
    Call<JsonObject> postUpdateSchedule(@Body JsonObject updateSche, @Path("scheId") int scheId, @Header("Authorization") String tokenData);

    //해당 유저에게 저장된 스케줄 검색
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET(BaseUrl.PATH_SEARCH_SCHEDULE)
    Call<ArrayList<JsonObject>> getSearchSchedule(@Header("Authorization") String tokenData, @Query(BaseUrl.PARAM_YEAR) int year);

    //스케줄 수락 ,거절
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST(BaseUrl.PATH_ASSENT_SCHEDULE)
    Call<JsonObject> postScheduleAssent(@Body JsonObject scheAssent, @Header("Authorization") String tokenData, @Path("scheId") int scheId);

}
