package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;
import android.util.Log;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-08-21
 * 스케줄 삭제 Realm
 */
public class RemoveScheduleRTask extends BaseAsyncTask<Boolean> {

    private long mSeq;

    Realm realm ;
    static final String TAG = RemoveScheduleRTask.class.getSimpleName();

    public RemoveScheduleRTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, long seq) {
        super(context, onTaskFinishedListener);

        mSeq = seq;
        Log.d(TAG, "mseq  remove ->" + mSeq);

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                realm.beginTransaction();
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class).equalTo("seq", mSeq).findAll();
                scheduleR.deleteAllFromRealm();

                realm.commitTransaction();
//                Log.d(TAG, "schedule data ->" + scheduleR.getTitle());
            }
        });
        return true;
    }
}
