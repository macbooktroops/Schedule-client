package com.playgilround.schedule.client.gson;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-23
 * ShareScheduleJsonData 가 끝나고,
 * 공유된 스케줄 json parsing 을 위해
 * JsonArray user parsing class
 */
public class ShareUserScheJsonData {
    @SerializedName("user")
    public JsonArray user;

    @SerializedName("id")
    public int user_id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("arrive")
    public boolean assent;

    @SerializedName("schedule_user_id")
    public int sche_id;
}
