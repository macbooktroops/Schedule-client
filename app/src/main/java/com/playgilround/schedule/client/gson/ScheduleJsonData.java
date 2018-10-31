package com.playgilround.schedule.client.gson;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-17
 */
public class ScheduleJsonData {

    @SerializedName("schedule")
    public JsonObject sJson;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("latitude")
    public double latitude;

    //스케줄 아이디
    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("longitude")
    public long longitude;

    //최초 생성자 정보
    @SerializedName("user")
    public JsonObject uJson;

    @SerializedName("name")
    public String name;

    @SerializedName("birth")
    public long birth;

    //schedule_users table
    @SerializedName("schedule_id")
    public int sche_id;

    @SerializedName("email")
    public String email;

}
