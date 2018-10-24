package com.playgilround.schedule.client.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-23
 * 공유 된 스케줄 Json Data
 */
public class ShareScheduleJsonData {

    @SerializedName("id")
    public int id;

    @SerializedName("state")
    public int state;

    @SerializedName("title")
    public String title;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("latitude")
    public long latitude;

    @SerializedName("longitude")
    public long longitude;


    //User Json
    @SerializedName("user")
    public JsonArray user;
}
