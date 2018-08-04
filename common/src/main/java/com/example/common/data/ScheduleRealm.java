package com.example.common.data;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.common.realm.RealmArrayList;
import com.example.common.realm.ScheduleR;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * 18-08-04
 * 실질적으로 Realm 작업을 진행하는 클래스.
 */
public class ScheduleRealm {

    static final String TAG = ScheduleRealm.class.getSimpleName();
    Context context;

    Realm realm;

    private ScheduleRealm(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }

    public static ScheduleRealm getInstance(Context context) {
        return new ScheduleRealm(context);
    }

    //insert schedule
    public void addSchedule(final ArrayList<RealmArrayList> realmArr) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //increase primary key "id?
                Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                int nextId;

                if (currentIdNum == null) {
                    nextId = 0;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                ScheduleR schedule = realm.createObject(ScheduleR.class, nextId);
                Log.d(TAG, "content schedule ==>" + realmArr.get(0).getContent());
                //                      Schedule schedule = new Schedule();
                schedule.setTitle(realmArr.get(0).getContent());
                schedule.setState(realmArr.get(0).getState());

                Log.d(TAG, "add schedule Realms");
                schedule.setTime(realmArr.get(0).getTime());
                schedule.setYear(realmArr.get(0).getYear());
                schedule.setMonth(realmArr.get(0).getMonth());
                schedule.setDay(realmArr.get(0).getDay());

                Log.d(TAG, "try AddScheduleTask ===");
            }
        });

    }
}

