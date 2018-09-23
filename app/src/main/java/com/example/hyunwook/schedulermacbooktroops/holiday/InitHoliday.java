package com.example.hyunwook.schedulermacbooktroops.holiday;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.AddEventSetRTask;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;

/**
 * 최초 앱 실행 시
 * Holiday EventSet 생성 클래스
 */
public class InitHoliday {
    /**
     * 최초 앱 실행 시 공휴일 EventSet 추가.
     * 클릭 시, 해당 연도 공휴일을 한번에 볼 수있음.
     */
    private void initHolidayEventSet() {

        int nYear;

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        nYear = calendar.get(Calendar.YEAR);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR holidayE = realm.where(EventSetR.class).equalTo("name", nYear+"년 공휴일").findFirst();
                Log.d(TAG, "check holidayE -> " +holidayE);

                Number currentIdNum = realm.where(EventSetR.class).max("seq");
                Log.d(TAG, "check currentIdNum -> " + currentIdNum);
                int nextId;

                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }


                if (holidayE == null) {
                    //최초로 판단.
                    EventSetR eventSet = realm.createObject(EventSetR.class, nextId);
                    eventSet.setName(nYear+ "년 공휴일");
                    eventSet.setColor(Color.CYAN);
                    //execute Save EventSet AsyncTask
                    new AddEventSetRTask(getApplicationContext(), new OnTaskFinishedListener<EventSetR>() {
                        @Override
                        public void onTaskFinished(EventSetR data) {
                            Log.d(TAG, "AddEventSetRTask holiday ->" + data.getName() + data.getColor());
                            /**
                             * Intent 전송 시
                             * EventSetR 구분은 항상 seq
                             */

                            if (eventSet != null) {
                                Log.d(TAG, "execute holiday eventSet");
                                mEventSetAdapter.insertItem(eventSet);
                            }
//                            setResult(ADD_EVENT_SET_FINISH, new Intent().putExtra(EVENT_SET_OBJ, data.getSeq()));
//                            finish();
                        }
                    }, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.d(TAG, "Already holiday created.");
                }
            }
        });
    }
}
