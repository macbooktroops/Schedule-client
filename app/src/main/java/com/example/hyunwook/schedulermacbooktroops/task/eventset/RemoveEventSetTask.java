package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.data.EventSetDB;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;

/**
 * 18-07-09
 * 이벤트 스케줄 분류 삭제 AsyncTask
 */
public class RemoveEventSetTask extends BaseAsyncTask<Boolean> {
    private int mId;

    public RemoveEventSetTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, int id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ScheduleDB sd = ScheduleDB.getInstance(mContext);
        sd.removeScheduleByEventSetId(mId);

        EventSetDB ed = EventSetDB.getInstance(mContext);
        return ed.removeEventSet(mId);
    }
}
