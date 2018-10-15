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

    //0 -> 자신한테 온 요청 1-> 자기가 건 요청
    @SerializedName("request")
    public boolean request;

    //1 요청중 , 2 친구
    @SerializedName("assent")
    public int assent;

    //id column
    @SerializedName("friend_id")
    public int friendId;
}
