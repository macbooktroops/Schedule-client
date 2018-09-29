package com.playgilround.common.base.task;

import android.content.Context;
import android.os.AsyncTask;

import com.playgilround.common.listener.OnTaskFinishedListener;

/**
 * 18-06-07
 * AsyncTask
 */
public abstract class BaseAsyncTask<T> extends AsyncTask<Void, Void, T> {

    protected Context mContext;
    protected OnTaskFinishedListener<T> mOnTaskFinishedListener;

    public BaseAsyncTask(Context context, OnTaskFinishedListener<T> onTaskFinishedListener) {
        mContext = context;
        mOnTaskFinishedListener = onTaskFinishedListener;
    }

    //작업 중
    @Override
    protected abstract T doInBackground(Void... params);

    //작업 마무리
    @Override
    protected void onPostExecute(T data) {
        super.onPostExecute(data);
        if (mOnTaskFinishedListener != null) {
            mOnTaskFinishedListener.onTaskFinished(data);
        }
    }
}
