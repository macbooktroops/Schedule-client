package com.example.hyunwook.schedulermacbooktroops.task.schedule;

import android.content.Context;
import android.util.EventLog;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.EventSet;
import com.example.common.bean.Schedule;
import com.example.common.data.ScheduleDB;
import com.example.common.listener.OnTaskFinishedListener;

import java.util.List;


import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.Schedule;
import com.example.common.listener.OnTaskFinishedListener;

import java.util.List;

/**
 * 16-06-07
 * 스케줄 리스트 얻기 AsyncTask
 */

public class LoadScheduleTask extends BaseAsyncTask<List<Schedule>> {


    private int mYear;
    private int mMonth;
    private int mDay;

    public LoadScheduleTask(Context context, OnTaskFinishedListener<List<Schedule>> onTaskFinishedListener, int year, int month , int day) {
        super(context, onTaskFinishedListener);

        mYear = year;
        mMonth = month;
        mDay = day;

    }


    //작업 중
    @Override
    protected List<Schedule> doInBackground(Void... params) {
        ScheduleDB db = ScheduleDB.getInstance(mContext);
        return db.getScheduleByDate(mYear, mMonth, mDay);
    }

}
