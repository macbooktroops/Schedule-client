package com.example.common.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 18-09-18
 * 작년, 올해, 내년 공휴일 정보가 있는 Realm
 */
public class HolidayR extends RealmObject {

    @PrimaryKey
    private int id;

    private int year;
    private int month;
    private int day;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
