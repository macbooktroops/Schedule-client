package com.playgilround.schedule.client.task.eventset;

import android.content.Context;
import android.util.Log;

import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;
import com.playgilround.schedule.client.base.task.BaseAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-08-18
 * ScheduleDetailActivity 에서 스케줄 항목 조회 AsyncTask
 * use Realm.
 */
public class LoadEventSetRMapTask extends BaseAsyncTask<Map<Integer, EventSetR>> {

    Realm realm;

    static final String TAG = LoadEventSetRMapTask.class.getSimpleName();


    Map<Integer, EventSetR> resultEventSet;

    EventSetR eventSetR;

    private Context mContext;

    public LoadEventSetRMapTask(Context context, OnTaskFinishedListener<Map<Integer, EventSetR>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        Log.d(TAG, "constructor loadevent");

        mContext = context;
    }

    @Override
    protected Map<Integer, EventSetR> doInBackground(Void... params) {

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<EventSetR> resultEvent = realm.where(EventSetR.class).findAll();

                List<EventSetR> resList = new ArrayList<>();
                resList.addAll(resultEvent);

                eventSetR = new EventSetR();
                resultEventSet = new HashMap<>();

                //사이즈 만큼 저장.
                for (EventSetR ev : resList) {
                    int seq = ev.getSeq();
                    String name = ev.getName();
                    int color = ev.getColor();
                    int icon = ev.getIcon();

                    eventSetR.setSeq(seq);
                    eventSetR.setName(name);
                    eventSetR.setColor(color);
                    eventSetR.setIcon(icon);
//                    Log.d(TAG, "Seq -->" + seq);
//                    eventSetR.setSeq(ev.getSeq());
//                    eventSetR.setName(ev.getName());
//                    eventSetR.setColor(ev.getColor());
//                    eventSetR.setIcon(ev.getIcon());
//
                    Log.d(TAG, "eventSetR ----> " +eventSetR.getSeq() +"--" + eventSetR.getName() + "-- "  + eventSetR.getColor() + "--" + eventSetR.getIcon());

                    resultEventSet.put(eventSetR.getSeq(), eventSetR);
                }

                Log.d(TAG, "resultEventSet data size -->" + resultEventSet.size());
            }
        });
        return resultEventSet;


    }
}
