package com.playgilround.schedule.client.task.schedule;

import android.content.Context;
import android.util.Log;

import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.ScheduleR;

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

        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (mSchedule != null) {

//                    Log.d(TAG, "mSchedule Check title --->" + mSchedule.getTitle());
                    Log.d(TAG, "mSchedule Check Desc --->" + mSchedule.getTitle());
                    Log.d(TAG, "mSchedule Check state --->" + mSchedule.getState());

                }

            }
        });

        return true;
    }
}
