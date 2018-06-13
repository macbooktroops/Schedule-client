package com.example.hyunwook.schedulermacbooktroops.fragment;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calendar.widget.calendar.OnCalendarClickListener;
import com.example.calendar.widget.calendar.schedule.ScheduleLayout;
import com.example.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.example.common.base.app.BaseFragment;
import com.example.hyunwook.schedulermacbooktroops.R;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.base.app.BaseFragment;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.MainActivity;

import java.util.Calendar;

/**
 * 18-05-25
 * 스케줄 작성 메인화면 하단뷰
 */

public class ScheduleFragment extends BaseFragment implements OnCalendarClickListener {

    private ScheduleLayout scheduleLayout;

    private ScheduleRecyclerView rvSchedule;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

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

//        scheduleLayout.setOnCalendarClickListener(this);

        initScheduleList();
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
        resetScheduleList();
    }

    @Override
    public void onPageChange(int year, int month ,int day) {

    }

    //스케줄 리스트 리셋
    public void resetScheduleList() {
        //병렬로 작업을 실행하는 데 사용할 수있는 실행
//        new LoadScheduleTask(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
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
//        rvSchedule = scheduleLayout.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        //item view가 추가/삭제/이동 할때 animation
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

    }
}
