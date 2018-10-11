package com.playgilround.schedule.client.friend.json;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * 18-10-11
 * 친구 수락 및 거절 클릭 push jsonData SerializedName
 */
public class FriendAssentJsonData {

    @SerializedName("friend")
    public JsonObject fJson;
}
