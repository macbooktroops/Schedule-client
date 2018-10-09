package com.playgilround.schedule.client.friend;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-09
 * 친구 신청 push jsonData
 */
public class FriendPushJsonData {

    @SerializedName("type")
    public JsonObject type;

    @SerializedName("user")
    public JsonObject user;
}
