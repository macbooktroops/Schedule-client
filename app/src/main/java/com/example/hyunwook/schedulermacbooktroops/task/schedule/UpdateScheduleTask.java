package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

/**
 * 18-06-27
 * 업데이트 스케줄
 */
public class UpdateScheduleTask extends BaseAsyncTask<Boolean> {

    private ScheduleR mSchedule;

    public UpdateScheduleTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, ScheduleR schedule) {
        super(context, onTaskFinishedListener);
        mSchedule = schedule;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (mSchedule != null) {
            ScheduleDB db = ScheduleDB.getInstance(mContext);
            return db.updateSchedule(mSchedule);
        } else {
            return false;
        }
    }
}
