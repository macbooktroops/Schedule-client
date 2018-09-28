package com.example.hyunwook.schedulermacbooktroops.login;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 18-09-27
 * Register Json SerializedName
 */
public class Result {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public List<String> message;

    // @SerializedName("message")
    // public JsonObject message;

//    @SerializedName("message")
//    private List<String> messageList;

  /*  public void setMessage(List<String> message) {
        this.message = message;
    }
    public List<String> getMessage() {
        return message;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
*/
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
