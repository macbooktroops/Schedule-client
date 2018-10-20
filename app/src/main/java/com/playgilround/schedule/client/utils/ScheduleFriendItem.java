package com.playgilround.schedule.client.utils;

/**
 * 18-10-20
 * Schedule 저장 시
 * 스케줄 공유 친구 저장되는 클래스?
 */
public class ScheduleFriendItem {

    private String name;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
