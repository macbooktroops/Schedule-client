package com.playgilround.common.bean;

import java.io.Serializable;

/**
 * 18-06-07
 * 객체 직렬화
 * 직렬로 만들어서 저장, 전송 등을 편하게 한다는 의미입니다.
 * 객체를 데이터 전송시킴.
 * https://medium.com/@henen/빠르게-배우는-안드로이드-intent-4-내가-만든-class를-전송-serializable-이용-5fddf7e3c730
 */
public class Schedule implements Serializable {

    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    }}
