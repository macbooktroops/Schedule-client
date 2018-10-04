package com.playgilround.calendar.widget.calendar.retrofit;

public class BaseUrl {

    public static final String BASE_URL = "http://schedule.mactroops.com";
//     public static final String BASE_URL = "http://192.168.219.104:3000";
    // public static final String BASE_URL = "http://localhost:3000";

    // Note:: Paths
    public static final String PATH_HOLIDAYS = "/v1/holidays";
    public static final String PATH_FCM_TOKEN = "/v1/fcm_token";

    public static final String PATH_SIGN_UP = "/users";
    public static final String PATH_SIGN_IN = "/users/sign_in";

    public static final String PATH_USER_SEARCH = "/v1/user";

    public static final String PATH_NEW_FRIEND = "/v1/friends/new";

    // Note:: Params
    public static final String PARAM_YEAR = "year";
}
