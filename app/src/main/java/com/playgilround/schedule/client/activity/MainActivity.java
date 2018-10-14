package com.playgilround.schedule.client.activity;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.common.base.app.BaseActivity;
import com.playgilround.common.base.app.BaseFragment;
import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.adapter.EventSetAdapter;
import com.playgilround.schedule.client.dialog.SelectHolidayDialog;
import com.playgilround.schedule.client.firebase.FirebaseMessagingService;
import com.playgilround.schedule.client.fragment.EventSetFragment;
import com.playgilround.schedule.client.friend.fragment.FriendFragment;
import com.playgilround.schedule.client.fragment.ScheduleFragment;

import com.playgilround.schedule.client.holiday.InitHoliday;
import com.playgilround.schedule.client.task.eventset.LoadEventSetRTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-05-24
 * Main
 *
 */
public class MainActivity extends BaseActivity
        implements View.OnClickListener, OnTaskFinishedListener<List<EventSetR>>, SelectHolidayDialog.OnHolidaySetListener  {

    private DrawerLayout drawMain;
    private LinearLayout linearDate;
    private TextView tvTitleMonth, tvTitleDay, tvTitle;

    private String[] mMonthText;

    private List<EventSetR> mEventSets;

    private BaseFragment mScheduleFragment, mEventSetFragment, mFriendFragment;
    private EventSetR mCurrentEventSet;

    static final String TAG = MainActivity.class.getSimpleName();

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    private EventSetAdapter mEventSetAdapter;

    private RecyclerView rvEventSetList;
    private AddEventSetBroadcastReceiver mAddEventSetBroadcastReceiver;

    Realm realm;

    List<EventSetR> resultEvent;

    SharedPreferences pref;
    public static String ADD_EVENT_SET_ACTION = "action.add.event.set";
    public static int ADD_EVENT_SET_CODE = 1;

    public static int ADD_EVENT_SET_FINISH = 2;
    public static String EVENT_SET_OBJ = "event.set.obj";


    private long[] mNotes = new long[2]; //back button save.

    private ProgressBar mProgressBar;

    static Activity activity;

    private int mYear;
    private SelectHolidayDialog mSelectHolidayDialog;
    String resPush; //push 를통해 앱 실행

    String resPushName; //푸쉬를 보낸 사람의 닉네임
    int resPushId; //푸쉬 아이디 friend id column

    String authToken;

    //foreground, background 판단
    public static boolean isAppRunning = false;
    @Override
    protected void bindView() {
        setContentView(R.layout.activity_main);

        activity = this;

        isAppRunning = true;
        drawMain = searchViewById(R.id.dlMain);
        linearDate = searchViewById(R.id.linearTitleDate);
        tvTitleMonth = searchViewById(R.id.tvTitleMonth);
        tvTitleDay = searchViewById(R.id.tvTitleDay);
        tvTitle = searchViewById(R.id.tvTitle);
        rvEventSetList = searchViewById(R.id.rvEventSetList);

        realm = Realm.getDefaultInstance();

        /**
         * Retrofit Presenter
         */
    /*    Call<Holiday> holiday = RetrofitHoliday.getInstance().getService().requestHoliInfo("2018");
        holiday.enqueue(new Callback<ArrayList<JsonObject>> () {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "result retrofit ->" + response.body().toString());
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });*/

        //각각 클릭 리스너 적용
        searchViewById(R.id.imgBtnMain).setOnClickListener(this);
        searchViewById(R.id.linearMenuSchedule).setOnClickListener(this);
        searchViewById(R.id.linearMenuFriends).setOnClickListener(this);
        searchViewById(R.id.linearMenuNoCategory).setOnClickListener(this);
        searchViewById(R.id.tvMenuAddEventSet).setOnClickListener(this);


//        getToken();
        initUI();
        initEventSetList();

        Intent intent = getIntent();
        resPush = intent.getStringExtra("pushData");
        resPushName = intent.getStringExtra("pushName");
        resPushId = intent.getIntExtra("pushId", 1);
        Log.d(TAG, "resPush -->" + resPush + "--"+ resPushName + "--" + resPushId);
        goScheduleFragment();
        initBroadcastReceiver();

        Log.d(TAG, "MainActivity State onCreate ---> " + MainActivity.isAppRunning);


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
        pref = getSharedPreferences("loginData", Activity.MODE_PRIVATE);
        Log.d(TAG, "login data check ->" + pref.getString("loginName", "") + "--" + pref.getString("loginToken", ""));
        //kitkat 이하
        if (Build.VERSION.SDK_INT < 19) {
            Log.d(TAG, "this phone below kitkat");
            TextView tvMenuTitle = searchViewById(R.id.tvMenuTitle);
            tvMenuTitle.setGravity(Gravity.CENTER_VERTICAL);
        }

    }

    public static Activity getActivity() {
        return activity;
    }

    //after execute bindView()
    @Override
    protected void initData() {
        super.initData();
        resetMainTitleDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        new LoadEventSetRTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        initHolidayEventSet();

        /**
         * 앱 실행 시, EventSetR에 해당 연도 공휴일 항목이 있는지 검사.
         */

        InitHoliday initHoliday = new InitHoliday();
        initHoliday.initHolidayEventSet();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity State onResume ---> " + MainActivity.isAppRunning);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity State onPause ---> " + MainActivity.isAppRunning);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity State onStop ---> " + MainActivity.isAppRunning);
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

        if (mFriendFragment != null) {
            ft.hide(mFriendFragment);
        }
        ft.show(mScheduleFragment);
        ft.commit();

        linearDate.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        drawMain.closeDrawer(Gravity.START);

//        Log.d(TAG, "FirebaseMessage isChkPush 3 -->" + FirebaseMessagingService.isChkPush);

        if (resPush == null) {

        } else if (resPush.equals("FriendPush")) {
//            Log.d(TAG, "FirebaseMessage isChkPush 2 -->" + FirebaseMessagingService.isChkPush);
            Intent intent = new Intent(this, FriendAssentActivity.class);
            intent.putExtra("PushName", resPushName);
            intent.putExtra("PushId", resPushId);
            startActivity(intent);
//        } else {
//            if (FirebaseMessagingService.isChkPush) {
//                Log.d(TAG, "FirebaseMessage isChkPush --->" + FirebaseMessagingService.isChkPush);
//                Intent intent = new Intent(this, FriendAssentActivity.class);
//                intent.putExtra("PushName", resPushName);
//                intent.putExtra("PushId", resPushId);
//                startActivity(intent);
//            }
        }

    }

    /**
     * SelectHolidayDialog 년도 선택 완료시 호출.
     * SelectHolidayDialog -> EventSetFragment 순
     * @param year
     * @param eventSet
     */
    @Override
    public void onHolidaySet(int year, EventSetR eventSet) {
        Log.d(TAG, "onHolidaySet --->" + year + "--"+ eventSet.getName());
        mYear = year;
        //mYear 로 검색한 공휴일 EventSetFragment show
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setTransition(FragmentTransaction.TRANSIT_NONE);

        //공휴일 년도 변경은 현재 EventSetR체크 할 필요없음.
//        if (mCurrentEventSet != eventSet || eventSet.getSeq() == 0 || mYear !=  ) {
            if (mEventSetFragment != null)
                ft.remove(mEventSetFragment);

            mEventSetFragment = EventSetFragment.getInstance(eventSet, mYear);
            ft.add(R.id.frameContainer, mEventSetFragment);
//        }

        if (mScheduleFragment != null) {
            ft.hide(mScheduleFragment); //스케줄 은 숨기고.
        }
        if (mFriendFragment != null) {
            ft.hide(mFriendFragment);
        }
        ft.show(mEventSetFragment); //스케줄 항목 프래그로.
        ft.commit();

        Log.d(TAG, "gotoEventSet getName ->" + eventSet);
        resetTitleText(eventSet.getName());
        drawMain.closeDrawer(Gravity.START);
        mCurrentEventSet = eventSet;

    }



    //goto eventset fragment
    //스케줄 작성 된 프레그먼트로.
    public void gotoEventSetFragment(EventSetR eventSet) {
//        Log.d(TAG, "gotoEventSetFragment -->" + eventSet.get());

        /**
         * -1이면 EventSetFragment 실행 전에 SelectHolidayDialog 표시 먼저
         */
        if (eventSet.getSeq() == -1) {
            Log.d(TAG, "start SelectHolidayDialog ----" + mSelectHolidayDialog);
            if (mSelectHolidayDialog == null)
                mSelectHolidayDialog = new SelectHolidayDialog(this, this, eventSet);

            mSelectHolidayDialog.show();
        } else {
            Log.d(TAG, "start Fragment----");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setTransition(FragmentTransaction.TRANSIT_NONE);

//        Log.d(TAG, "eventSet --->" + eventSet.getSeq() + "---" + eventSet.getId());
            if (mCurrentEventSet != eventSet || eventSet.getSeq() == 0) {
                if (mEventSetFragment != null)
                    ft.remove(mEventSetFragment);

                mEventSetFragment = EventSetFragment.getInstance(eventSet);
                ft.add(R.id.frameContainer, mEventSetFragment);
            }

            if (mScheduleFragment != null) {
                ft.hide(mScheduleFragment); //스케줄 은 숨기고.
            }
            if (mFriendFragment != null) {
                ft.hide(mFriendFragment);
            }
            ft.show(mEventSetFragment); //스케줄 항목 프래그로.
            ft.commit();

            Log.d(TAG, "gotoEventSet getName ->" + eventSet);
            resetTitleText(eventSet.getName());
            drawMain.closeDrawer(Gravity.START);
            mCurrentEventSet = eventSet;
        }
    }

    //친구 관련 프레그먼트
    public void goFriendFragment() {
        Log.d(TAG, "goFriendFragment ----->" + mFriendFragment + "--" + resPush);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setTransition(FragmentTransaction.TRANSIT_NONE);

       /* if (mFriendFragment != null) {
            ft.remove(mFriendFragment);
        }*/

        //친구 추가 push 를 통해 FriendFragment 에 접속 할 경우

//        if (resPush == null) {
        if (mFriendFragment == null) {
            mFriendFragment = FriendFragment.getInstance();
            ft.add(R.id.frameContainer, mFriendFragment);
        }
//        else if (resPush.equals("FriendPush")) {
//            tvTitle.setText("친구 관련");
//            mFriendFragment = FriendFragment.getInstance(resPush);
//        }

        if (mScheduleFragment != null) {
            ft.hide(mScheduleFragment);
        }

        if (mEventSetFragment != null) {

            ft.hide(mEventSetFragment);
        }
        ft.show(mFriendFragment);
        ft.commit();

        Log.d(TAG, "get Text ...->" + tvTitle.getText().toString());
        resetTitleText("친구 관련");
        drawMain.closeDrawer(Gravity.START);

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
                Log.d(TAG, "linearMenu");
                goScheduleFragment();
                break;

            case R.id.linearMenuFriends:
                Log.d(TAG, "linear Friend");
                goFriendFragment();
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
                final int eventSetId = (int) data.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ);
                Log.d(TAG, "EventSetId --->" + eventSetId);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "get realm test ---");
                        EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", eventSetId).findFirst();
                        Log.d(TAG, "eventSetR --->" + eventSetR.getName());

                        if (eventSetR != null) {
                            mEventSetAdapter.insertItem(eventSetR);
                        }
                    }
                });
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
        isAppRunning = false;
        Log.d(TAG, "MainActivity State onDestroy---> " + MainActivity.isAppRunning);

        if (mAddEventSetBroadcastReceiver != null) {
            unregisterReceiver(mAddEventSetBroadcastReceiver);
            mAddEventSetBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTaskFinished(List<EventSetR> data) {
//        realm = Realm.getDefaultInstance();

        resultEvent = new ArrayList<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<EventSetR> eventSetR = realm.where(EventSetR.class).findAll();

                List<EventSetR> resList = new ArrayList<>();
                resList.addAll(eventSetR);

                resultEvent = resList;

            }
        });

        mEventSetAdapter.changeAllData(resultEvent);
    }
    //메뉴 계획 항목 추가 리시버
    private class AddEventSetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ADD_EVENT_SET_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "AddEventSetBroadcastReceiver ----");

                final int eventSetId = (int) intent.getSerializableExtra(AddEventSetActivity.EVENT_SET_OBJ);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "get realm test receiver ---");
//                        long seq = realm.where(EventSetR.class).max("seq").longValue();
                        EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", eventSetId).findFirst();

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
