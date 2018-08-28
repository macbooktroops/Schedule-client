package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;
import android.util.Log;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-07-09
 * 이벤트 스케줄 분류 삭제 AsyncTask
 */
public class RemoveEventSetRTask extends BaseAsyncTask<Boolean> {
    private int mSeq;
    Realm resRealm;

    static final String TAG = RemoveEventSetRTask.class.getSimpleName();

    List<ScheduleR> resSchedule;
    public RemoveEventSetRTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, int seq, Realm realm) {
        super(context, onTaskFinishedListener);
        mSeq = seq;
        resRealm = realm;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
//        ScheduleDB sd = ScheduleDB.getInstance(mContext);
//        sd.removeScheduleByEventSetId(mId);
        resRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                        .equalTo("eventSetId", mSeq).findAll();

                Log.d(TAG, "remove task schedule ->" + scheduleR);
            }
        });
        return true;
//        EventSetDB ed = EventSetDB.getInstance(mContext);
//        return ed.removeEventSet(mId);
    }
}
