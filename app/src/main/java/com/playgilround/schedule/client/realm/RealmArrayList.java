package com.playgilround.schedule.client.realm;

/**
 * 18-08-04
 * Realm DB를 위한 사용자 정의 ArrayList
 */
public class RealmArrayList {

    String content; //사용자 작성내용
    int state; //상태

    long time;
    int year, month, day;

    //ScheduleFragment Add Schedule constructor
    public RealmArrayList(String content, int state, long time, int year, int month, int day) {
        this.content = content;
        this.state = state;
        this.time = time;
        this.year = year;
        this.month = month;
        this.day = day;
    }


    public String getContent() {
        return content;
    }

    public int getState() {
        return state;
    }

    public long getTime() {
        return time;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
