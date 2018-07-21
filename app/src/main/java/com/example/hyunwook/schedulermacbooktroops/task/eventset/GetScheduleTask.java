package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;

import java.util.List;

/**
 * Created by hyunwook on 2018-07-20.
 */

public class GetScheduleTask extends BaseAsyncTask<List<Schedule>> {

    private int mId;

    public GetScheduleTask(Context context, OnTaskFinishedListener<List<Schedule>> onTaskFinishedListener, int id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected List<Schedule> doInBackground(Void... params) {
        ScheduleDB db = ScheduleDB.getInstance(mContext);
        return db.getScheduleByEventSetId(mId);
    }
}
