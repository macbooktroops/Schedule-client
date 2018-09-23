package com.example.hyunwook.schedulermacbooktroops.holiday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;
import com.example.hyunwook.schedulermacbooktroops.activity.MainActivity;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.AddEventSetRTask;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;

/**
 * 최초 앱 실행 시
 * Holiday EventSet 생성 클래스
 */
public class InitHoliday {
    Realm realm;
    Activity mActivity;

    public static int ADD_EVENT_SET_FINISH = 2;
    public static String EVENT_SET_OBJ = "event.set.obj";

    static final String TAG = InitHoliday.class.getSimpleName();

    /**
     * 최초 앱 실행 시 공휴일 EventSet 추가.
     * 클릭 시, 해당 연도 공휴일을 한번에 볼 수있음.
     */
    public void initHolidayEventSet() {
        mActivity = MainActivity.getActivity();
        realm = Realm.getDefaultInstance();
        int nYear;

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        nYear = calendar.get(Calendar.YEAR);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR holidayE = realm.where(EventSetR.class).equalTo("name", nYear+"년 공휴일").findFirst();
                Log.d(TAG, "check holidayE -> " +holidayE);

                if (holidayE == null) {
                    //최초로 판단.
                    EventSetR eventSet = realm.createObject(EventSetR.class, -1);
                    eventSet.setName(nYear+ "년 공휴일");
                    eventSet.setColor(-1);
                    //execute Save EventSet AsyncTask
                    new AddEventSetRTask(mActivity.getApplicationContext(), new OnTaskFinishedListener<EventSetR>() {
                        @Override
                        public void onTaskFinished(EventSetR data) {
                            Log.d(TAG, "AddEventSetRTask holiday ->" + data.getName() + data.getColor());
                        }
                    }, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.d(TAG, "Already holiday created.");
                }
            }
        });
    }
}
