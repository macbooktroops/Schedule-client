package com.playgilround.schedule.client.gson;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-11
 * 친구 수락 및 거절 클릭 push jsonData SerializedName
 */
public class FriendAssentJsonData {

    @SerializedName("friend")
    public JsonObject fJson;

    @SerializedName("user_id")
    public int id;

    @SerializedName("is_friend_at")
    public String friendAt;

    @SerializedName("user_name")
    public String name;

    @SerializedName("is_friend")
    public int friend;

}
