package com.playgilround.common.find;

import com.google.gson.annotations.SerializedName;

/**
 * 18-10-07
 * Json Create Find Email Data class
 */
public class EmailJsonData {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("birth")
    public long birth;

}
