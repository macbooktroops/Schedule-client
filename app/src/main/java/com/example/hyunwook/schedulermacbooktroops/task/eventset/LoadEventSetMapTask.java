package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.EventSet;
import com.example.common.data.EventSetDB;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;

import java.util.Map;

/**
 * 18-08-18
 * ScheduleDetailActivity 에서 스케줄 항목 조회 AsyncTask
 * use Realm.
 */
public class LoadEventSetMapTask extends BaseAsyncTask<Map<Integer, EventSetR>> {

    private Context mContext;

    public LoadEventSetMapTask(Context context, OnTaskFinishedListener<Map<Integer, EventSetR>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        mContext = context;
    }

    @Override
    protected Map<Integer, EventSetR> doInBackground(Void... params) {
        EventSetDB db = EventSetDB.getInstance(mContext);
        return db.getAllEventSetMap();
    }
}
