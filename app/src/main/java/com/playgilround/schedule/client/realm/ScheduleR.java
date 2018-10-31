package com.playgilround.schedule.client.realm;



import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * 18-08-01
 * Schedule 관련 DB use Realm
 */
public class ScheduleR extends RealmObject {


    @PrimaryKey
    private int seq;

    private int id;

    private int scheId; //schedule table 에 schedule_id (server)

    private int userId; //schedule_user table에 user_id (server)

    private String nickName; //users table에 name (server)

    private String email; //users table에 email (server)

    private boolean assent; //도착여부
    private int color;
    private String title;
    private String desc;
    private String location;

    private int state;
    private long time;
    private int year;
    private int month;
    private int day;
    private int eventSetId;

    private double latitude;
    private double longitude;
    private String hTime;

    private String sendServer; //서버 전송여부


    /* public ScheduleR(String content, int state, long time, int year, int month, int day) {
         this.title = content;
         this.state = state;
         this.time = time;
         this.year = year;
         this.month = month;
         this.day = day;

     }

 */
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScheId() {
        return scheId;
    }

    public void setScheId(int scheId) {
        this.scheId = scheId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getAssent() {
        return assent;
    }

    public void setAssent(boolean assent) {
        this.assent = assent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc == null ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String gethTime() {
        return hTime;
    }

    public void sethTime(String hTime) {
        this.hTime = hTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getEventSetId() {
        return eventSetId;
    }

    public void setEventSetId(int eventSetId) {
        this.eventSetId = eventSetId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSendServer() {
        return sendServer;
    }

    public void setSendServer(String sendServer) {
        this.sendServer = sendServer;
    }
}