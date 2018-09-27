package com.example.hyunwook.schedulermacbooktroops.login;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 18-09-27
 * Register Json SerializedName
 */
public class Message {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

//    @SerializedName("message")
//    private List<String> messageList;

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

//    public void setMessage(List<String> message) {
//        this.message = message;
//    }

//    public void setMessageList(List<String> messageList) {
//        this.messageList = messageList;
//    }
//    public List<String> getMessageList() {
//        return messageList;
//    }
}
