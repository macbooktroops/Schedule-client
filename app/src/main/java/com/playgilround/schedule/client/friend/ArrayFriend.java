package com.playgilround.schedule.client.friend;

import java.util.ArrayList;

/**
 * 18-10-12
 * Friend 목록을 가지고있는 배열 클래스
 */
public class ArrayFriend {

    String mName;
    String mBirth;

    public ArrayFriend(String name, String birth) {
        mName = name;
        mBirth = birth;
    }

    public String getName() {
        return mName;
    }

    public String getBirth() {
        return mBirth;
    }
}
