package com.example.hyunwook.schedulermacbooktroops.task.eventset;

import android.content.Context;
import android.util.Log;

import com.example.common.base.task.BaseAsyncTask;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;

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

                    resultEventSet.put(seq, eventSetR);
                }

                Log.d(TAG, "resultEventSet data size -->" + resultEventSet.size());
            }
        });
        return resultEventSet;


    }
}
