package com.example.hyunwook.schedulermacbooktroops.login;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 18-09-28
 * Message class 에서 gson 작업 한 데이터를
 * 한번 더 Serialized.
 */
public class Message2 {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public List<String> message;
}
