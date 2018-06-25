package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;

import com.example.common.data.ScheduleDB;
import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;

/**
 * 18-06-22
 * Schedule 저장 AsyncTask
 */
public class AddScheduleTask extends BaseAsyncTask<Schedule> {

    private Schedule mSchedule;

    public AddScheduleTask(Context context, com.example.common.listener.OnTaskFinishedListener<Schedule> onTaskFinishedListener, Schedule schedule) {
        super(context, onTaskFinishedListener);
        mSchedule = schedule;
    }

    //AsyncTask 실행 중
    @Override
    protected Schedule doInBackground(Void... params) {
        if (mSchedule != null) {
            ScheduleDB sd = ScheduleDB.getInstance(mContext);

            int id = sd.addSchedule(mSchedule);

            if (id != 0) {
                mSchedule.setId(id);
                return mSchedule;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
