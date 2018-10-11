package com.playgilround.schedule.client.friend.json;

import com.google.gson.annotations.SerializedName;

/**
 * 18-10-04
 * Json Create User Search Data class
 * id, name, email, birth
 */
public class UserJsonData {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("birth")
    public long birth;

    @SerializedName("is_friend")
    public int isFriend;
}
