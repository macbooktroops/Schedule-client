package com.playgilround.schedule.client.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 18-10-31
 * 도착완료 후,
 * 등수 매김을 위한 Arrived_at Json Data
 */
public class ArrivedAtJsonData {

    @SerializedName("name")
    public String name;

    @SerializedName("arrived_at")
    public String arriveTime;

}
