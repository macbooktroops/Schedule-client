package com.playgilround.common.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 18-08-02
 * EventSet 관련 DB use Realm
 * https://realm.io/kr/docs/get-started/installation/mac/   macOS Realm browser
 */
public class EventSetR extends RealmObject {

    @PrimaryKey
    private int seq;

    private int id;

    private String name;
    private int color;
    private int icon;
    private boolean isChecked;

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
