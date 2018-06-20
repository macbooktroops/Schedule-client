package com.example.hyunwook.schedulermacbooktroops.activity;


import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.app.BaseActivity;
import com.example.common.base.app.BaseFragment;
import com.example.common.bean.EventSet;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.fragment.ScheduleFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 18-05-24
 * Main
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private DrawerLayout drawMain;
    private LinearLayout linearDate;
    private TextView tvTitleMonth, tvTitleDay, tvTitle;

    private String[] mMonthText;

    private List<EventSet> mEventSets;

    private BaseFragment mScheduleFragment;

    static final String TAG = MainActivity.class.getSimpleName();

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;
    @Override
    protected void bindView() {
        setContentView(R.layout.activity_main);
        drawMain = searchViewById(R.id.dlMain);
        linearDate = searchViewById(R.id.linearTitleDate);
        tvTitleMonth = searchViewById(R.id.tvTitleMonth);
        tvTitleDay = searchViewById(R.id.tvTitleDay);
        tvTitle = searchViewById(R.id.tvTitle);

        //각각 클릭 리스너 적용
        searchViewById(R.id.imgBtnMain).setOnClickListener(this);
        searchViewById(R.id.linearMenuSchedule).setOnClickListener(this);

        initUI();
    }

    //UI 초기화
    private void initUI() {
        drawMain.setScrimColor(Color.TRANSPARENT); //drawer 열려있는 동안 색상

        mMonthText = getResources().getStringArray(R.array.calendar_month); //월 배열
        linearDate.setVisibility(View.VISIBLE); //뷰 보여짐
        tvTitle.setVisibility(View.GONE); //뷰 보여지지않음.
        tvTitleMonth.setText(mMonthText[Calendar.getInstance().get(Calendar.MONTH)]);
        Log.d(TAG, "mMonthText -->" + mMonthText[Calendar.getInstance().get(Calendar.MONTH)]);

        tvTitleDay.setText(getString(R.string.calendar_today));

        //kitkat 이하
        if (Build.VERSION.SDK_INT < 19) {
            Log.d(TAG, "this phone below kitkat");
            TextView tvMenuTitle = searchViewById(R.id.tvMenuTitle);
            tvMenuTitle.setGravity(Gravity.CENTER_VERTICAL);
        }

        initEventSetList();
    }

    //RecyclerView 설정
    private void initEventSetList() {
        Log.d(TAG, "initEventSetList...");
        mEventSets = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL); //세로 배치

        //item view가 추가/삭제/이동 할때 animation
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false); //비활성화

        goScheduleFragment();

    }

    //goto ScheduleView Fragment()
    private void goScheduleFragment() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE); //Fragment 에서 애니메이션 효과 없이..

        if (mScheduleFragment == null) {
            //get Instance ScheduleFragment...
            Log.d(TAG, "goSchedule instance");
            mScheduleFragment = ScheduleFragment.getInstance();
            ft.add(R.id.frameContainer, mScheduleFragment);
        }
        ft.show(mScheduleFragment);
        ft.commit();

        linearDate.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        drawMain.closeDrawer(Gravity.START);

        initBroadcastReceiver();

    }

    private void initBroadcastReceiver() {
//        if ()
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //메뉴 버튼 클릭
            case R.id.imgBtnMain:
                drawMain.openDrawer(Gravity.START);
                break;

            case R.id.linearMenuSchedule:
//                goScheduleFragment();
                break;
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
}
