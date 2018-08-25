package com.example.hyunwook.schedulermacbooktroops.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.example.common.base.app.BaseFragment;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;
import com.example.common.realm.ScheduleR;
import com.example.common.util.DeviceUtils;
import com.example.common.util.ToastUtils;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.ScheduleDetailActivity;
import com.example.hyunwook.schedulermacbooktroops.adapter.ScheduleAdapter;
import com.example.hyunwook.schedulermacbooktroops.dialog.SelectDateDialog;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.GetScheduleRTask;
import com.example.hyunwook.schedulermacbooktroops.task.schedule.AddScheduleRTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-06-19
 * 스케줄 목록을 보고자 할때 표시되는 프레그먼트
 *
 */
public class EventSetFragment extends BaseFragment implements View.OnClickListener,
        ScheduleAdapter.ScheduleEvent,
        SelectDateDialog.OnSelectDateListener, OnTaskFinishedListener<List<ScheduleR>> {

    static final String TAG = EventSetFragment.class.getSimpleName();
    private ScheduleRecyclerView rvScheduleList;
    private EditText etInput;
    private RelativeLayout rlNoTask;

    private ScheduleAdapter mScheduleAdapter;

    private EventSetR mEventSet;

    private SelectDateDialog mSelectDateDialog;

    private int mPosition = -1;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;
    private long mTime;

    RealmResults<ScheduleR> resEmp;
    List<ScheduleR> resList = new ArrayList<>();


    //realm 에 time을 보기 편하게 변환
    private String HUMAN_TIME_FORMAT = "";
    private String resultTime;
    public static String EVENT_SET_OBJ = "event.set.obj";

    Realm realm;

    static EventSetR resultEvent; //EVENT_SET_OBJ
    /**
     * http://milkissboy.tistory.com/34
     * @param eventSet
     * @return
     */
    public static EventSetFragment getInstance(EventSetR eventSet) {
        EventSetFragment fragment = new EventSetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_SET_OBJ, eventSet.getSeq()); //객체 넘기기
        fragment.setArguments(bundle);
        resultEvent = eventSet;
        return fragment;
    }

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_event_set, container, false);
    }

    @Override
    protected void bindView() {
        rvScheduleList = searchViewById(R.id.rvScheduleList);
        rlNoTask = searchViewById(R.id.rlNoTask);
        etInput = searchViewById(R.id.etInputContent);

        HUMAN_TIME_FORMAT = getString(R.string.human_time_format);
        realm = Realm.getDefaultInstance();
        searchViewById(R.id.ibMainClock).setOnClickListener(this);
        searchViewById(R.id.ibMainOK).setOnClickListener(this);
        initBottomInputBar();
        initScheduleList();
    }

    //스케줄 메모 부분 InputBar
    private void initBottomInputBar() {

        //http://egloos.zum.com/killins/v/3008925  --> TextWatcher
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //start 지점에서 시작되는 count 갯수만큼의
                //글자들이 after 길이만큼의 글자로 대치되려고 할 때 호출된다

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //start 지점에서 시작되는 before 갯수만큼의 글자들이
                //count 갯수만큼의 글자들로 대치되었을 때 호출된다.

            }

            //EditText의 텍스트가 변경되면 호출된다.
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged --->" + s.toString());
                etInput.setGravity(s.length() == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
            }
        });

        etInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    //스케줄 리스트 설정
    private void initScheduleList() {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvScheduleList.setLayoutManager(manager);

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        rvScheduleList.setItemAnimator(itemAnimator);

        //스케줄 어댑터 설정
        mScheduleAdapter = new ScheduleAdapter(mActivity, this);
        rvScheduleList.setAdapter(mScheduleAdapter);


    }

    @Override
    protected void initData() {
        super.initData();
//        Log.d(TAG, "mEvent init -->" + mEventSet);
     /*   final int mEventSetId = (int) getArguments().getSerializable(EVENT_SET_OBJ);
        Log.d(TAG, "mEventSetId -->" + mEventSetId);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventSetR eventSetR = realm.where(EventSetR.class).equalTo("seq", mEventSetId).findFirst();
                mEventSet = eventSetR;

                Log.d(TAG, "mEventSet fragment ->" + mEventSet.getName());
            }
        });*/
        mEventSet = resultEvent;

    }

    @Override
    protected void bindData() {
        super.bindData();
        Log.d(TAG, "mEventSet -->" + mEventSet.getSeq());
        new GetScheduleRTask(mActivity, this, mEventSet.getSeq()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMainClock:
                showSelectDateDialog();
                break;
            case R.id.ibMainOK:
                addSchedule();
                break;
        }
    }

    //시작 시간 설정 다이얼로그.
    private void showSelectDateDialog() {
        if (mSelectDateDialog == null) {
            Calendar calendar = Calendar.getInstance();
            mSelectDateDialog = new SelectDateDialog(mActivity, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mPosition);
        }

        mSelectDateDialog.show();
    }
//키보드 닫기
    private void closeSoftInput() {
        etInput.clearFocus();
        DeviceUtils.closeSoftInput(mActivity, etInput);
    }

    //ok버튼 클릭 시 스케줄 등록
    private void addSchedule() {
        final String content = etInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShortToast(mActivity, R.string.schedule_input_null);
        } else {
            closeSoftInput();

            realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                        Log.d(TAG, "addSchedule EventSetFragment");

                        Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                        int nextId;

                        if (currentIdNum == null) {
                            nextId = 0;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }
                        //스케줄 저장
                        ScheduleR schedule = realm.createObject(ScheduleR.class, nextId);
                        Log.d(TAG, "eventFragment content ==>" + content);
                        schedule.setTitle(content);
                        schedule.setState(0);
                        schedule.setColor(mEventSet.getColor());
                        schedule.setEventSetId(mEventSet.getId());
                        schedule.setTime(mTime);
                        schedule.sethTime(resultTime);
                        schedule.setYear(mCurrentSelectYear);
                        schedule.setMonth(mCurrentSelectMonth);
                        schedule.setDay(mCurrentSelectDay);

                        new AddScheduleRTask(mActivity, new OnTaskFinishedListener<ScheduleR>() {
                            @Override
                            public void onTaskFinished(ScheduleR data) {
                                Log.d(TAG, "EventSetFragment add" + data);

                                if (data != null) {
                                    mScheduleAdapter.insertItem(data);
                                    etInput.getText().clear();
                                    rlNoTask.setVisibility(View.GONE);
                                    mTime = 0;
                                }
                            }
                        }, schedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);




                    }
            });
        }

    }

    //현재 날짜
    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    @Override
    public void onSelectDate(int year, int month, int day, long time, int position) {
//        Log.d(TAG, "onSelectData -->" +month);
        setCurrentSelectDate(year, month + 1, day);

        mTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
        resultTime = sdf.format(mTime);

        Log.d(TAG, "mTime -->" + mTime + "--" + resultTime);
        mPosition = position;
    }

    /**
     * data 대신 resultEvent 값을 받아 처리해야함.
     *
     * @param data
     */
    @Override
    public void onTaskFinished(List<ScheduleR> data) {
        Log.d(TAG, "Event Task Finish -->" + mEventSet.getSeq());


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "get realm");
                resEmp = realm.where(ScheduleR.class).equalTo("eventSetId", mEventSet.getSeq()).findAll();
                Log.d(TAG, "resEmp Get -->" +  resEmp.size());

                resList.addAll(resEmp);   //resList에 해당 스케줄 항목인 스케줄들을 추가.
            /*    for (ScheduleR sd : resList) {
                    int id = sd.getId();
                    int color = sd.getColor();
                    String title = sd.getTitle();
                    String desc = sd.getDesc();
                    String location = sd.getLocation();
                    int state = sd.getState();
                    int year = sd.getYear();
                    int month = sd.getMonth();
                    int day = sd.getDay();
                    long time = sd.getTime();
                    int eventId = sd.getEventSetId();
                    String hTime = sd.gethTime();


                }*/

            }
        });
        Log.d(TAG, "resList -->" + resList);
        mScheduleAdapter.changeAllData(resList);

        rlNoTask.setVisibility(resList.size() == 0 ? View.VISIBLE : View.GONE);

                resList.clear();

    }

    @Override
    public void onClick(ScheduleR schedule) {

        Log.d(TAG, "schedule fragment -->" + schedule.getSeq());
        startActivity(new Intent(getActivity(), ScheduleDetailActivity.class)
                                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule.getSeq()) //primary key seq
                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION,  -1));
    }

    @Override
    public void onReset() {

    }
}
