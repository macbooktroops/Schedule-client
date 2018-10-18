package com.playgilround.schedule.client.task.eventset;

import android.content.Context;
import android.util.Log;

import com.playgilround.schedule.client.base.task.BaseAsyncTask;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.EventSetR;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-08-18
 * 저장된 스케줄 항목 얻기.
 * use Realm
 */
public class LoadEventSetRTask extends BaseAsyncTask<List<EventSetR>> {

    private Context mContext;

    Realm realm;

    List<EventSetR> resultEvent;

    static final String TAG = LoadEventSetRTask.class.getSimpleName();

    public LoadEventSetRTask(Context context, OnTaskFinishedListener<List<EventSetR>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        mContext = context;
    }

    @Override
    public List<EventSetR> doInBackground(Void... params) {
       /* realm = Realm.getDefaultInstance();

        resultEvent = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "Load test");
                RealmResults<EventSetR> eventSetR = realm.where(EventSetR.class).findAll();

                List<EventSetR> resList = new ArrayList<>();
                resList.addAll(eventSetR);

                resultEvent = resList;

            }
        });

        Log.d(TAG, "resultEvent ---->" + resultEvent.size());*/
       Log.d(TAG, "doIn LoadEventSetRTask");
        return resultEvent;
    }
}
