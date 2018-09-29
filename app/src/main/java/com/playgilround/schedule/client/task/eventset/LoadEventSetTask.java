package com.playgilround.schedule.client.task.eventset;

import android.content.Context;

import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.bean.EventSet;
import com.playgilround.common.data.EventSetDB;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;

import java.util.List;

/**
 * 18-07-02
 * 저장된 이벤트 태스트 얻기
 */
public class LoadEventSetTask extends BaseAsyncTask<List<EventSetR>> {

    private Context mContext;

    public LoadEventSetTask(Context context, OnTaskFinishedListener<List<EventSetR>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        mContext = context;
    }

    @Override
    protected List<EventSetR> doInBackground(Void... params) {
        EventSetDB db = EventSetDB.getInstance(mContext);
        return db.getAllEventSet();
    }
}
