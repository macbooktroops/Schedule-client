package com.playgilround.schedule.client.retrofit;

public class BaseUrl {

//    public static final String BASE_URL = "http://schedule.mactroops.com";
//    public static final String BASE_URL = "http://schedule-prod.ap-northeast-2.elasticbeanstalk.com/";

     public static final String BASE_URL = "http://172.16.6.203:3000";
//
//     public static final String BASE_URL = "http://192.168.0.140:3000";
//     public static final String BASE_URL = "http://192.168.3.243:3000/";

//     public static final String BASE_URL = "http://localhost:3000";

    // Note:: Paths
    public static final String PATH_HOLIDAYS = "/v1/holidays"; //v1/holidays?year=2018
    public static final String PATH_FCM_TOKEN = "/v1/fcm_token";

    public static final String PATH_SIGN_UP = "/users";
    public static final String PATH_SIGN_IN = "/users/sign_in";

    public static final String PATH_USER_SEARCH = "/v1/user";

    public static final String PATH_NEW_FRIEND = "/v1/friends/new";

    public static final String PATH_FRIEND_SEARCH = "/v1/friends";

    public static final String PATH_FIND_EMAIL = "v1/user/email";

    public static final String PATH_FIND_PASSWORD = "v1/user/password";

    public static final String PATH_RESET_PASSWORD = "/v1/user/password/new";

    public static final String PATH_FRIEND_ASSENT = "/v1/friends/{pushId}/consent";


    //schedule
    public static final String PATH_UPDATE_SCHEDULE = "/v1/schedule/edit/{scheId}";

    public static final String PATH_NEW_SCHEDULE = "/v1/schedule/new";

    public static final String PATH_SEARCH_SCHEDULE = "/v1/schedule";

    public static final String PATH_DETAIL_SCHEDULE = "/v1/schedule/{scheId}";

    public static final String PATH_DELETE_SCHEDULE = "/v1/schedule/{scheId}";

    public static final String PATH_ARRIVE_SCHEDULE = "/v1/schedule/{scheId}/arrive";

    public static final String PATH_ASSENT_SCHEDULE = "/v1/schedule/{scheId}/consent";


    // Note:: Params
    public static final String PARAM_YEAR = "year";

//    public static final String PARAM_FRIEND_ID = "/consent";
}
