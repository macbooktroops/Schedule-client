package com.playgilround.schedule.client.friend.json;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-09
 * 친구 신청 push jsonData 최초 SerializedName
 */
public class FriendPushJsonData {

    @SerializedName("type")
    public String type;

    @SerializedName("user")
    public JsonObject user;

    @SerializedName("friend_id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("birth")
    public long birth;

    @SerializedName("email")
    public String email;
}
