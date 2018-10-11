package com.playgilround.schedule.client.friend.json;

import com.google.gson.annotations.SerializedName;

/**
 * 18-10-11
 * Json Create User Search Data class
 * id, name, email, birth
 */
public class UserSearchJsonData {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("birth")
    public String birth;

    @SerializedName("assent")
    public boolean isAssent;
}

