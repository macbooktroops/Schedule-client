package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.ScheduleR;

import java.util.List;

/**
 * Created by hyunwook on 2018-07-20.
 * 저장된 스케줄 전부 가져오는 태스크.
 */

public class GetScheduleTask extends BaseAsyncTask<List<ScheduleR>> {

    private int mId;

    public GetScheduleTask(Context context, OnTaskFinishedListener<List<ScheduleR>> onTaskFinishedListener, int id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected List<ScheduleR> doInBackground(Void... params) {
        ScheduleDB db = ScheduleDB.getInstance(mContext);
        return db.getScheduleByEventSetId(mId);
    }
}
