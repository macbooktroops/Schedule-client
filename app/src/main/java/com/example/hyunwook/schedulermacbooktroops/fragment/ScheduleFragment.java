package com.example.hyunwook.schedulermacbooktroops.fragment;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.calendar.widget.calendar.OnCalendarClickListener;
import com.example.calendar.widget.calendar.schedule.ScheduleLayout;
import com.example.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.example.common.base.app.BaseFragment;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.RealmArrayList;
import com.example.common.realm.ScheduleR;
import com.example.common.util.DeviceUtils;
import com.example.common.util.ToastUtils;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.MainActivity;
import com.example.hyunwook.schedulermacbooktroops.activity.ScheduleDetailActivity;
import com.example.hyunwook.schedulermacbooktroops.adapter.ScheduleAdapter;
import com.example.hyunwook.schedulermacbooktroops.dialog.SelectDateDialog;
import com.example.hyunwook.schedulermacbooktroops.task.schedule.AddScheduleRTask;
import com.example.hyunwook.schedulermacbooktroops.task.schedule.LoadScheduleRTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 18-05-25
 * 스케줄 작성 메인화면 하단뷰
 */

public class ScheduleFragment extends BaseFragment implements OnCalendarClickListener,
        View.OnClickListener, ScheduleAdapter.ScheduleEvent,
        SelectDateDialog.OnSelectDateListener, OnTaskFinishedListener<List<ScheduleR>> {

    private ScheduleLayout scheduleLayout;

    private ScheduleRecyclerView rvSchedule;

    private EditText etInputContent;

    private RelativeLayout rlNoTask;
    private ScheduleAdapter mScheduleAdapter;

    private long mTime;

    //제대로 저장되었는지 확인 사이즈
    RealmResults<ScheduleR> resSchedule;
    static final String TAG = ScheduleFragment.class.getSimpleName();

    //Realm 정의 ArrayList
    ArrayList<RealmArrayList> arrSchedule;
    private String HUMAN_TIME_FORMAT = "";

    private String resultTime;

//    private ScheduleAdapter

    Realm realm;
    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    List<ScheduleR> schedule;

    public static ScheduleFragment getInstance() {
        return new ScheduleFragment();
    }
    //create fragment init
    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    protected void bindView() {
        scheduleLayout = searchViewById(R.id.sdLayout);
        etInputContent = searchViewById(R.id.etInputContent); //스케줄 입력 창

        rlNoTask = searchViewById(R.id.rlNoTask);
        scheduleLayout.setOnCalendarClickListener(this);

        realm = Realm.getDefaultInstance();

        HUMAN_TIME_FORMAT = getString(R.string.human_time_format);

        searchViewById(R.id.ibMainClock).setOnClickListener(this);
        searchViewById(R.id.ibMainOK).setOnClickListener(this);
        initScheduleList();
        initBottomInputBar();

    }

    @Override
    protected void initData() {
        super.initData();
        initDate();
    }

    //DateSetting
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        setCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void bindData() {
        super.bindData();
        resetScheduleList();
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        setCurrentSelectDate(year, month, day);
        Log.d(TAG, "onClickDate -------");
        resetScheduleList();
    }

    @Override
    public void onPageChange(int year, int month ,int day) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMainClock:
                showSelectDateDialog();
                //시간 설정이 가능한 다이얼로그?
                break;
            case R.id.ibMainOK:
                addSchedule();
                break;
        }
    }

    //스케줄 추가
    private void addSchedule() {
        final String content = etInputContent.getText().toString();

        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShortToast(mActivity, R.string.schedule_input_content_is_no_null);
        } else {
            closeSoftInput();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d(TAG, "Try save");

                    Number currentIdNum = realm.where(ScheduleR.class).max("seq");

                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    ScheduleR schedule = realm.createObject(ScheduleR.class, nextId);
                    schedule.setTitle(content);
                    schedule.setState(0);
                    schedule.setTime(mTime);
                    schedule.sethTime(resultTime);
                    schedule.setYear(mCurrentSelectYear);
                    schedule.setMonth(mCurrentSelectMonth +1);
                    schedule.setDay(mCurrentSelectDay);

                    new AddScheduleRTask(mActivity, new OnTaskFinishedListener<ScheduleR>() {
                        @Override
                        public void onTaskFinished(ScheduleR data) {
                            Log.d(TAG, "AddScheduleTask finish ->" + data);
                            if (data != null) {
                                mScheduleAdapter.insertItem(data);
                                etInputContent.getText().clear();
                                rlNoTask.setVisibility(View.GONE);
                                mTime = 0;
                                resultTime = "0";
                                updateTaskHintUi(mScheduleAdapter.getItemCount() - 2);
                            }
                        }
                    }, schedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });

        }
    }



    private void closeSoftInput() {
        etInputContent.clearFocus();
        DeviceUtils.closeSoftInput(mActivity, etInputContent);
    }

    //스케줄 작성 전 시작 시간을 적을 수 있는 다이얼로그.
    private void showSelectDateDialog() {
        Log.d(TAG, "scheduleFrags -> " + mCurrentSelectMonth);
         new SelectDateDialog(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay,
                 scheduleLayout.getMonthCalendar().getCurrentItem()).show();
    }
    //스케줄 리스트 리셋
    public void resetScheduleList() {
        //병렬로 작업을 실행하는 데 사용할 수있는 실행
//        new LoadScheduleTask(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadScheduleRTask(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth +1, mCurrentSelectDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    //현재 년월일로 세팅
    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;

        //객체 검사
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).resetMainTitleDate(year, month, day);
        }
    }
    //스케줄 리스트 생성
    private void initScheduleList() {
        rvSchedule = scheduleLayout.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSchedule.setLayoutManager(manager);
        Log.d(TAG, "rvSchedule --->" + rvSchedule);
//        rvSchedule.setLayoutManager(manager);
        //item view가 추가/삭제/이동 할때 animation
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        rvSchedule.setItemAnimator(itemAnimator);


        mScheduleAdapter = new ScheduleAdapter(mActivity, this);
        rvSchedule.setAdapter(mScheduleAdapter);

    }

    //스케줄 메모 부분 InputBar
     private void initBottomInputBar() {

        //http://egloos.zum.com/killins/v/3008925  --> TextWatcher
        etInputContent.addTextChangedListener(new TextWatcher() {
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
                etInputContent.setGravity(s.length() == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
            }
        });

        etInputContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
     }

     //SelectDateDialog override
    @Override
    public void onSelectDate(final int year, final int month, final int day, long time, int position) {
        scheduleLayout.getMonthCalendar().setCurrentItem(position);

        scheduleLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scheduleLayout.getMonthCalendar().getCurrentMonthView().clickThisMonth(year, month, day);
            }
        }, 100);
        mTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat(HUMAN_TIME_FORMAT);
        resultTime = sdf.format(mTime);


    }

    /**
     *
     *  문제로 인해 asynctask가 끝난후 Realm Transaction 실행
     * @param data
     */
    @Override
    public void onTaskFinished(final List<ScheduleR> data) {
//        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Log.d(TAG, "getScheduleByDate ------ " + mCurrentSelectYear + "--" + mCurrentSelectMonth +1 + "--" + mCurrentSelectDay);
                RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                        .equalTo("year", mCurrentSelectYear)
                        .equalTo("month", mCurrentSelectMonth +1)
                        .equalTo("day", mCurrentSelectDay).findAll();
                Log.d(TAG, "check size ->" +scheduleR.size());

                schedule = data;
                Log.d(TAG, "ScheduleFragment onTaskFinished --> " + scheduleR);
                mScheduleAdapter.changeAllData(scheduleR);

                //스케줄이 하나도 없으면 이미지.
                rlNoTask.setVisibility(scheduleR.size() == 0 ? View.VISIBLE : View.GONE);
                updateTaskHintUi(scheduleR.size());


            }
        });

    }

    private void updateTaskHintUi(int size) {
        if (size == 0) {
            scheduleLayout.removeTaskHint(mCurrentSelectDay);
        } else {
            scheduleLayout.addTaskHint(mCurrentSelectDay);
        }
    }

    public int getCurrentCalendarPosition() {
        Log.d(TAG, "getCurrentCalendar -->" + scheduleLayout.getMonthCalendar().getCurrentItem());
        return scheduleLayout.getMonthCalendar().getCurrentItem();
    }

    @Override
    public void onClick(ScheduleR schedule) {
        Log.d(TAG," schedule onClick --->" + schedule.getSeq());

        startActivity(new Intent(getActivity(), ScheduleDetailActivity.class)
                //https://stackoverflow.com/questions/40648380/how-to-pass-data-via-intent-in-realm
                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule.getSeq()) //primary key seq
                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION, getCurrentCalendarPosition()));
    }

    @Override
    public void onReset() {
        Log.d(TAG, "onReset");
        resetScheduleList();
    }
}
