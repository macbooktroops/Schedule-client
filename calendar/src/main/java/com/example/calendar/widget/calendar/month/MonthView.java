package com.example.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.calendar.R;

import java.util.Calendar;
import java.util.jar.Attributes;

/**
 * 18-05-27
 * Month 월마다 화면 뷰
 */
public class MonthView extends View {

    private Paint mPaint;
    private Paint mLunarPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mHintCircleColor;
    private int mLunarTextColor;
    private int mHolidayTextColor;
    private int mLastOrNextMonthTextColor;

    private int mDaySize;
    private int mLunarTextSize;

    private boolean mIsShowLunar;
    private boolean mIsShowHint;
    private boolean mIsShowHolidayHint;

    private int mSelYear, mSelMonth, mSelDay; //선택한 연월

    private int mCurrYear, mCurrMonth, mCurrDay; //현재  연월일
    private DisplayMetrics mDisplayMetrics;

    private GestureDetector mGestureDetector; //터치 이벤트처
    static final String TAG = MonthView.class.getSimpleName();
    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this (context, array , attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttrs, int year, int month) {
        super(context, attrs, defStyleAttrs);

        initAttrs(array, year, month);
    }

    //월 뷰 기본색상설정
    private void initAttrs(TypedArray array, int year, int month) {
        Log.d(TAG, "initAttrs array -->" + array.toString());
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_text_color, Color.parseColor("FFFFFF")); //white
            mSelectBGColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_color, Color.parseColor("#E8E8E8")); //살짝 흰색? 그레이
            mSelectBGTodayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_today_color, Color.parseColor("#FF8594")); //분홍빨강

            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#575471")); //흑색에 가까운 블루
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#FF8594")); //분홍 빨강
            mHintCircleColor = array.getColor(R.styleable.MonthCalendarView_month_hint_circle_color, Color.parseColor("#FE8595")); //분홍 빨강
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#ACA9BC")); //흑색 블루

            mLunarTextColor = array.getColor(R.styleable.MonthCalendarView_month_lunar_text_color, Color.parseColor("#ACA9BC")); //흑색 블루
            mHolidayTextColor = array.getColor(R.styleable.MonthCalendarView_month_holiday_color, Color.parseColor("#A68BFF")); //보라색

            mDaySize = array.getInteger(R.styleable.MonthCalendarView_month_day_text_size, 13);
            mLunarTextSize = array.getInteger(R.styleable.MonthCalendarView_month_day_lunar_text_size, 8);

            mIsShowHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.MonthCalendarView_month_show_lunar, true);
            mIsShowHolidayHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_holiday_hint, true);
        } else {

            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mSelectBGTodayColor = Color.parseColor("#FF8594");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLastOrNextMonthTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#A68BFF");
            mDaySize = 13;
            mLunarTextSize = 8;
            mIsShowHint = true;
            mIsShowLunar = true;
            mIsShowHolidayHint = true;
        }
        mSelYear = year;
        mSelMonth = month;

        Log.d(TAG, "mSel Year/Month --> " + mSelYear + "-" + mSelMonth);


        initPaint();
    }

    //스케줄 화면 텍스트 세팅
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);

        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);

        initMonth();
    }

    //스케줄 월 날짜 세팅
    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);

        Log.d(TAG, "check initMonth -->" + mCurrYear + "/" + mCurrMonth + "/" + mCurrDay );

        //현재 연월이 선택한 연월이랑 같을경우
        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }

        initGestureDetector();
    }

    //선택연월
    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }


    //터치 이벤트 설정
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override //화면 터치 모든이벤트의 시작
            public boolean onDown(MotionEvent e) {
                return true;
            }


            //한번 터치일 경우 발생 된다
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    //해당위치를 터치할 경우
    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;

        int row = y / mRow
    }



}
