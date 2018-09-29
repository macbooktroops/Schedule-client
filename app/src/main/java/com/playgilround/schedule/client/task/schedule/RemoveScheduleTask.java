package com.playgilround.schedule.client.task.schedule;

import android.content.Context;

import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.data.ScheduleDB;
import com.playgilround.common.listener.OnTaskFinishedListener;

/**
 * 18-06-27
 * 스케줄 삭제 작업
 */
public class RemoveScheduleTask extends BaseAsyncTask<Boolean> {

    private long mId;

    public RemoveScheduleTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, long id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ScheduleDB db = ScheduleDB.getInstance(mContext);
        return db.removeSchedule(mId);
    }
}
