package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;
import android.util.Log;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

import io.realm.Realm;

/**
 * 스케줄 변경사항 업데이트 태스크
 * use Realm
 */
public class UpdateScheduleRTask extends BaseAsyncTask<Boolean> {

    private ScheduleR mSchedule;
    static final String TAG = UpdateScheduleRTask.class.getSimpleName();
    Realm realm;

    public UpdateScheduleRTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, ScheduleR schedule) {
        super(context, onTaskFinishedListener);
        mSchedule = schedule;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

    /*    realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (mSchedule != null) {

                    Log.d(TAG, "mSchedule Check title --->" + mSchedule.getTitle());
                    Log.d(TAG, "mSchedule Check Desc --->" + mSchedule.getDesc());
                }

            }
        });*/

        return true;
    }
}
