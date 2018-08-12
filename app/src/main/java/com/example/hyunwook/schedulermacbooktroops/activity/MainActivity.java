package com.example.hyunwook.schedulermacbooktroops.activity;


import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.base.app.BaseActivity;
import com.example.common.base.app.BaseFragment;
import com.example.common.bean.EventSet;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.adapter.EventSetAdapter;
import com.example.hyunwook.schedulermacbooktroops.fragment.EventSetFragment;
import com.example.hyunwook.schedulermacbooktroops.fragment.ScheduleFragment;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.LoadEventSetTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import io.realm.Realm;

/**
 * 18-05-24
 * Main
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, OnTaskFinishedListener<List<EventSetR>> {

    private DrawerLayout drawMain;
    private LinearLayout linearDate;
    private TextView tvTitleMonth, tvTitleDay, tvTitle;

    private String[] mMonthText;

    private List<EventSetR> mEventSets;

    private BaseFragment mScheduleFragment, mEventSetFragment;
    private EventSetR mCurrentEventSet;

    static final String TAG = MainActivity.class.getSimpleName();

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    private EventSetAdapter mEventSetAdapter;

    private RecyclerView rvEventSetList;
    private AddEventSetBroadcastReceiver mAddEventSetBroadcastReceiver;

    Realm realm;

    public static String ADD_EVENT_SET_ACTION = "action.add.event.set";
    public static int ADD_EVENT_SET_CODE = 1;
    private long[] mNotes = new long[2]; //back button save.
    @Override
    protected void bindView() {
        setContentView(R.layout.activity_main);
        drawMain = searchViewById(R.id.dlMain);
        linearDate = searchViewById(R.id.linearTitleDate);
        tvTitleMonth = searchViewById(R.id.tvTitleMonth);
        tvTitleDay = searchViewById(R.id.tvTitleDay);
        tvTitle = searchViewById(R.id.tvTitle);
        rvEventSetList = searchViewById(R.id.rvEventSetList);

        realm = Realm.getDefaultInstance();
        //각각 클릭 리스너 적용
        searchViewById(R.id.imgBtnMain).setOnClickListener(this);
        searchViewById(R.id.linearMenuSchedule).setOnClickListener(this);
        searchViewById(R.id.linearMenuNoCategory).setOnClickListener(this);
        searchViewById(R.id.tvMenuAddEventSet).setOnClickListener(this);

        initUI();
        initEventSetList();
        goScheduleFragment();
        initBroadcastReceiver();

    }

    //UI 초기화
    private void initUI() {
        drawMain.setScrimColor(Color.TRANSPARENT); //drawer 열려있는 동안 색상 메뉴아닌부분 색상 선택.

        mMonthText = getResources().getStringArray(R.array.calendar_month); //월 배열
        linearDate.setVisibility(View.VISIBLE); //뷰 보여짐
        tvTitle.setVisibility(View.GONE); //뷰 보여지지않음.
        tvTitleMonth.setText(mMonthText[Calendar.getInstance().get(Calendar.MONTH)]); //오늘월
        Log.d(TAG, "mMonthText -->" + mMonthText[Calendar.getInstance().get(Calendar.MONTH)]);

        tvTitleDay.setText(getString(R.string.calendar_today));

        //kitkat 이하
        if (Build.VERSION.SDK_INT < 19) {
            Log.d(TAG, "this phone below kitkat");
            TextView tvMenuTitle = searchViewById(R.id.tvMenuTitle);
            tvMenuTitle.setGravity(Gravity.CENTER_VERTICAL);
        }

    }

    //after execute bindView()
    @Override
    protected void initData() {
        super.initData();
        resetMainTitleDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        new LoadEventSetTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //RecyclerView 설정
    private void initEventSetList() {
        Log.d(TAG, "initEventSetList...");
        mEventSets = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL); //세로 배치

        rvEventSetList.setLayoutManager(manager);

        /**
         * DefaultItemAnimator는 item들의
         * 추가/삭제/이동 이벤트에 대한 기본적인 animation을 제공하는 클래스
         * RecyclerView는 이를 기본적인 ItemAnimator로 사용
         */
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false); //비활성화

        rvEventSetList.setItemAnimator(itemAnimator);

        mEventSetAdapter = new EventSetAdapter(this, mEventSets);
        rvEventSetList.setAdapter(mEventSetAdapter);

    }

    //goto ScheduleView Fragment()
    private void goScheduleFragment() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE); //Fragment 에서 애니메이션 효과 없이..

        if (mScheduleFragment == null) {
            //get Instance ScheduleFragment...
            Log.d(TAG, "goSchedule instance");
            mScheduleFragment = ScheduleFragment.getInstance(); //singleton
            ft.add(R.id.frameContainer, mScheduleFragment);
        }

        //ScheduleFragment show일때는 EventSetFragment hide.
        if (mEventSetFragment != null) {
            ft.hide(mEventSetFragment);
        }
        ft.show(mScheduleFragment);
        ft.commit();

        linearDate.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        drawMain.closeDrawer(Gravity.START);


    }



    //goto eventset fragment
    //스케줄 작성 된 프레그먼트로.
    public void gotoEventSetFragment(EventSetR eventSet) {
        Log.d(TAG, "gotoEventSetFragment");
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setTransition(FragmentTransaction.TRANSIT_NONE);

        if (mCurrentEventSet != eventSet || eventSet.getId() == 0) {
            if (mEventSetFragment != null)
                ft.remove(mEventSetFragment);

            mEventSetFragment = EventSetFragment.getInstance(eventSet);
            ft.add(R.id.frameContainer, mEventSetFragment);
        }

        ft.hide(mScheduleFragment); //스케줄 은 숨기고.
        ft.show(mEventSetFragment); //스케줄 항목 프래그로.
        ft.commit();

        Log.d(TAG, "gotoEventSet getName ->" + eventSet.getName());
        resetTitleText(eventSet.getName());
        drawMain.closeDrawer(Gravity.START);
        mCurrentEventSet = eventSet;
    }

    private void initBroadcastReceiver() {

        if (mAddEventSetBroadcastReceiver == null) {
            mAddEventSetBroadcastReceiver = new AddEventSetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ADD_EVENT_SET_ACTION);

            registerReceiver(mAddEventSetBroadcastReceiver, filter);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //메뉴 버튼 클릭
            case R.id.imgBtnMain:
                drawMain.openDrawer(Gravity.START);
                break;

            case R.id.linearMenuSchedule:
                goScheduleFragment();
                break;

            case R.id.linearMenuNoCategory:
                mCurrentEventSet = new EventSetR();
                mCurrentEventSet.setName(getString(R.string.menu_schedule_category));
                gotoEventSetFragment(mCurrentEventSet);
                break;

            case R.id.tvMenuAddEventSet:
                gotoAddEventSetActivity();
                break;
        }
    }

    private void gotoAddEventSetActivity() {
        startActivityForResult(new Intent(this, AddEventSetActivity.class), ADD_EVENT_SET_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EVENT_SET_CODE) {
            if (resultCode == AddEventSetActivity.ADD_EVENT_SET_FINISH) {
                Log.d(TAG, "check onActivityResult");
                //최근 EventSetR에 추가된 데이터를 어댑터에 추가

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "get realm test ---");
                        long seq = realm.where(EventSetR.class).max("seq").longValue();


                        EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", seq).findFirst();
                        Log.d(TAG, "eventSetR --->" + eventSetR.getName());

                        if (eventSetR != null) {
                            mEventSetAdapter.insertItem(eventSetR);
                        }
                    }
                });
//                EventSet eventSet = (EventSet) data.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ);
//                if (eventSet != null) {
//                    mEventSetAdapter.insertItem(eventSet);
//                }
//            }
            }
        }
    }

    //메인 타이틀에 년월일 재설정
    public void resetMainTitleDate(int year, int month, int day) {
        linearDate.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();

        if (year == calendar.get(Calendar.YEAR) &&
                month == calendar.get(Calendar.MONTH) &&
                day == calendar.get(Calendar.DAY_OF_MONTH)) {

            tvTitleMonth.setText(mMonthText[month]);
            tvTitleDay.setText(getString(R.string.calendar_today));
        } else {
            if (year == calendar.get(Calendar.YEAR)) {
                tvTitleMonth.setText(mMonthText[month]);
            } else {
                //매개 년도랑 현재 년도가 다름?
                tvTitleMonth.setText(String.format("%s%s", String.format(getString(R.string.calendar_year), year),
                        mMonthText[month]));
            }
            tvTitleDay.setText(String.format(getString(R.string.calendar_day), day));
        }
        setCurrentSelectDate(year, month, day);
    }

    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    //Title Text reset.
    private void resetTitleText(String name) {
        linearDate.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(name);
    }

    //back button click.
    @Override
    public void onBackPressed() {
        if (drawMain.isDrawerOpen(Gravity.START)) {
            drawMain.closeDrawer(Gravity.START);
        } else {
            //http://forum.falinux.com/zbxe/index.php?document_srl=571358&mid=lecture_tip
            System.arraycopy(mNotes, 1, mNotes, 0, mNotes.length -1);
            mNotes[mNotes.length - 1] = SystemClock.uptimeMillis();
            Log.d(TAG, "mNotes value --> " + mNotes[mNotes.length - 1]);
            if (SystemClock.uptimeMillis() - mNotes[0] < 1000) {
                finish();
            } else {
                Toast.makeText(this, getString(R.string.exit_app_hint), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAddEventSetBroadcastReceiver != null) {
            unregisterReceiver(mAddEventSetBroadcastReceiver);
            mAddEventSetBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTaskFinished(List<EventSetR> data) {
        mEventSetAdapter.changeAllData(data);
    }

    //메뉴 계획 항목 추가 리시버
    private class AddEventSetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ADD_EVENT_SET_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "AddEventSetBroadcastReceiver ----");
             /*   EventSet eventSet = (EventSet) intent.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ);
                if (eventSet != null) {
                    mEventSetAdapter.insertItem(eventSet);
                }*/

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "get realm test receiver ---");
                        long seq = realm.where(EventSetR.class).max("seq").longValue();


                        EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", seq).findFirst();
                        Log.d(TAG, "eventSetR receiver--->" + eventSetR.getName());

                        if (eventSetR != null) {
                            mEventSetAdapter.insertItem(eventSetR);
                        }
                    }
                });
                }
            }
        }

}
