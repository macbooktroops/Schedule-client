package com.playgilround.schedule.client.task.schedule;

import android.content.Context;
import android.util.Log;

import com.playgilround.common.data.ScheduleRealm;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.ScheduleR;
import com.playgilround.schedule.client.base.task.BaseAsyncTask;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-08-09
 * Realm 스케줄 리스트 얻기 AsyncTask
 * 해당 연월일 기준으로 스케줄 검색
 */
public class LoadScheduleRTask extends BaseAsyncTask<List<ScheduleR>> {

    private int mYear;
    private int mMonth;
    private int mDay;

    Realm realm;

    List<ScheduleR> resSchedule;

    static final String TAG = LoadScheduleRTask.class.getSimpleName();

    public LoadScheduleRTask(Context context, OnTaskFinishedListener<List<ScheduleR>> onTaskFinishedListener,
                             int year, int month, int day) {
        super(context, onTaskFinishedListener);


        mYear = year;
        mMonth = month;
        mDay = day;
        Log.d(TAG, "Load -->" + year + "--" + month + "--"+ day);
//
    }

    //작업 중
    @Override
    protected List<ScheduleR> doInBackground(Void... params) {
        Log.d(TAG, "doIn Load");
//        ScheduleRealm
        /*realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "doInBackground");
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                        .equalTo("year", mYear)
                        .equalTo("month", mMonth)
                        .equalTo("day", mDay).findAll();
                Log.d(TAG, "Check size --> " + scheduleR.size());

                resSchedule = scheduleR;
            }
        });*/

        Log.d(TAG, "resSchedule -->" + resSchedule);
        return resSchedule;

//       Log.d(TAG, "doInLoad");
//       ScheduleRealm sr = ScheduleRealm.getInstance(mContext);
//       return sr.getScheduleByDate(mYear, mMonth, mDay);
    }
}
