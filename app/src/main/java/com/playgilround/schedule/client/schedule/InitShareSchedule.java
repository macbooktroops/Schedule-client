package com.playgilround.schedule.client.schedule;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.playgilround.schedule.client.activity.MainActivity;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.EventSetR;
import com.playgilround.schedule.client.task.eventset.AddEventSetRTask;

import io.realm.Realm;

/**
 * 18-10-28
 * 공유된 스케줄 EventSetR 저장
 */
public class InitShareSchedule {

    Realm realm;
    Activity mActivity;

    static final String TAG = InitShareSchedule.class.getSimpleName();

    /**
     * 최초 앱 실행 시 공유된 스케줄 EvetSet 추가.
     * 좌측 메뉴 클릭 시, 해당 연도 공유된 스케줄 볼 수있음.
     */
    public void shareScheEventSet() {
        mActivity = MainActivity.getActivity();
        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR shareE = realm.where(EventSetR.class).equalTo("name", "공유").findFirst();
                Log.d(TAG, "check shareE ->" +shareE);

                if (shareE == null) {
                    //최초로 판단.
                    EventSetR eventSet = realm.createObject(EventSetR.class, -2);
                    eventSet.setName("공유");
                    eventSet.setColor(-2);
                    new AddEventSetRTask(mActivity.getApplicationContext(), new OnTaskFinishedListener<EventSetR>() {
                        @Override
                        public void onTaskFinished(EventSetR data) {
                            Log.d(TAG, "AddEventSetRTask shareE ->" + data.getName() + data.getColor());
                        }
                    }, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.d(TAG, "Already shareE created.");
                }
            }
        });
    }
}
