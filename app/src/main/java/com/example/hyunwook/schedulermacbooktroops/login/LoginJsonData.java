package com.example.hyunwook.schedulermacbooktroops.login;

import com.google.gson.annotations.SerializedName;

/**
 * 18-09-26
 * Json Create Login Data class
 * name, auth_token
 */
public class LoginJsonData {

    @SerializedName("name")
    public String name;

    @SerializedName("auth_token")
    public String token;
}
