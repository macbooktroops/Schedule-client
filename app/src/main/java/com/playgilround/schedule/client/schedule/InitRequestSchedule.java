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
 * 18-11-02
 * 공유 요청 들어온 스케줄 EventSetR 저장
 */
public class InitRequestSchedule {

    Realm realm;
    Activity mActivity;

    static final String TAG = InitRequestSchedule.class.getSimpleName();

    /**
     * 최초 앱 실행 시 공유요청이 들어온 스케줄 EventSetR 추가
     * 좌측 메뉴 클릭 시, 해당 연도 공유 요청들어온 스케줄 볼 수 있음.
     */
    public void requestScheEventSet() {
        mActivity = MainActivity.getActivity();
        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR requestE = realm.where(EventSetR.class).equalTo("name", "요청 스케줄").findFirst();
                Log.d(TAG, "check requestE ->" + requestE);

                if (requestE == null) {
                    //최초로 판단
                    EventSetR eventSetR = realm.createObject(EventSetR.class, -3);
                    eventSetR.setName("요청 스케줄");
                    eventSetR.setColor(-3);
                    new AddEventSetRTask(mActivity.getApplicationContext(), new OnTaskFinishedListener<EventSetR>() {
                        @Override
                        public void onTaskFinished(EventSetR data) {
                            Log.d(TAG, "AddEventSetRTask requestE ->" + data.getName() + data.getColor());
                        }
                    }, eventSetR).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.d(TAG, "Already requestE created..");
                }
            }
        });
    }

}
