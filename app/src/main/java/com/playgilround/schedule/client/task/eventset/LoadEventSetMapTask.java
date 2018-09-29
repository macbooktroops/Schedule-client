package com.playgilround.schedule.client.task.eventset;

import android.content.Context;

import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.bean.EventSet;
import com.playgilround.common.data.EventSetDB;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;

import java.util.Map;

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
