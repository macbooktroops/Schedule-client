package com.playgilround.schedule.client.task.eventset;

import android.content.Context;

import com.playgilround.common.base.task.BaseAsyncTask;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;

/**
 * 18-08-12
 * Realm 스케줄 항목(분류) 추가시 EventSetR에 저장되는 클래스
 */
public class AddEventSetRTask extends BaseAsyncTask<EventSetR> {

    static final String TAG = AddEventSetRTask.class.getSimpleName();

    private EventSetR mEventSetR;

    public AddEventSetRTask(Context context, OnTaskFinishedListener<EventSetR> onTaskFinishedListener, EventSetR eventSetR) {
        super(context, onTaskFinishedListener);

        mEventSetR = eventSetR;
    }

    @Override
    protected EventSetR doInBackground(Void... params) {
        if (mEventSetR != null) {

        }
        return mEventSetR;
    }
}
