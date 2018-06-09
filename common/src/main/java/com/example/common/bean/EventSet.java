package com.example.common.bean;

import java.io.Serializable;

/**
 * 18-05-24
 * 객체 직렬화
 * 직렬로 만들어서 저장, 전송 등을 편하게 한다는 의미입니다.
 * 객체를 데이터 전송시킴.
 * https://medium.com/@henen/빠르게-배우는-안드로이드-intent-4-내가-만든-class를-전송-serializable-이용-5fddf7e3c730
 */
public class EventSet implements Serializable {

    private int id;
    private String name;
    private int color;
    private int icon;
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
