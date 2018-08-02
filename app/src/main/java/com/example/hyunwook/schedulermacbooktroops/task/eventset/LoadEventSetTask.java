package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.bean.EventSet;
import com.example.common.data.EventSetDB;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;

import java.util.List;

/**
 * 18-07-02
 * 저장된 이벤트 태스트 얻기
 */
public class LoadEventSetTask extends BaseAsyncTask<List<EventSet>> {

    private Context mContext;

    public LoadEventSetTask(Context context, OnTaskFinishedListener<List<EventSet>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        mContext = context;
    }

    @Override
    protected List<EventSet> doInBackground(Void... params) {
        EventSetDB db = EventSetDB.getInstance(mContext);
        return db.getAllEventSet();
    }
}
