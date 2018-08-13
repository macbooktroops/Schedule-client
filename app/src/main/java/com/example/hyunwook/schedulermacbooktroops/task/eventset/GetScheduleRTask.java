package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;
import android.util.Log;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-08-12
 * EventSetFragment 에서 해당 이벤트 스케줄분류로 등록 된
 * 스케줄 얻기 Realm
 * Seq기준
 */

public class GetScheduleRTask extends BaseAsyncTask<List<ScheduleR>>{

    private int mSeq;

    static final String TAG = GetScheduleRTask.class.getSimpleName();
    List<ScheduleR> resList = new ArrayList<>();

    RealmResults<ScheduleR> resEmp;
    Realm realm;
    public GetScheduleRTask(Context context, OnTaskFinishedListener<List<ScheduleR>> onTaskFinishedListener, int seq) {
        super(context, onTaskFinishedListener);
        mSeq = seq;
    }

    @Override
    protected List<ScheduleR> doInBackground(Void... params) {
        Log.d(TAG, "check GetScheduleRTask --> " + mSeq);

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "get realm");
                resEmp = realm.where(ScheduleR.class).equalTo("eventSetId", mSeq).findAll();
                Log.d(TAG, "resEmp Get -->" +  resEmp.size());

                resList.addAll(resEmp);   //resList에 해당 스케줄 항목인 스케줄들을 추가.
/*
                for (ScheduleR sd : resList) {
                    String id = sd.getId();
                }*/

            }
        });
        Log.d(TAG, "resList -->" + resList);

        return resList;
    }



}
