package com.playgilround.schedule.client.task.schedule;

import android.content.Context;

import com.playgilround.common.data.ScheduleDB;
import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.bean.Schedule;
import com.playgilround.common.realm.ScheduleR;

/**
 * 18-06-22
 * Schedule 저장 AsyncTask
 */
public class AddScheduleTask extends BaseAsyncTask<ScheduleR> {

    private ScheduleR mSchedule;

    public AddScheduleTask(Context context, com.playgilround.common.listener.OnTaskFinishedListener<ScheduleR> onTaskFinishedListener, ScheduleR schedule) {
        super(context, onTaskFinishedListener);
        mSchedule = schedule;
    }

    //AsyncTask 실행 중
    @Override
    protected ScheduleR doInBackground(Void... params) {
        if (mSchedule != null) {
            ScheduleDB sd = ScheduleDB.getInstance(mContext);

            int id = sd.addSchedule(mSchedule);
//            int id = 1;
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
