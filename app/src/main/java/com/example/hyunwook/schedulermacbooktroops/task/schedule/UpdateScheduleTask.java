package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;

/**
 * 18-06-27
 */
public class UpdateScheduleTask extends BaseAsyncTask<Boolean> {

    private Schedule mSchedule;

    public UpdateScheduleTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, Schedule schedule) {
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
