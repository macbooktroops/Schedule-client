package com.playgilround.schedule.client.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.calendar.CalendarUtils;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

/**
 * 18-06-03
 */

public class WeekView extends View {

    static final String TAG = WeekView.class.getSimpleName();
    private static final int NUM_COLUMNS = 7;

    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mHintCircleColor;
    private int mLunarTextColor;
    private int mHolidayTextColor;
    private int mCircleRadius = 6;

    private int mDaySize;
    private int mLunarTextSize;
    private boolean mIsShowLunar;
    private boolean mIsShowHint;
    private boolean mIsShowHolidayHint;

    private DateTime mStartDate;
    private Bitmap mRestBitmap, mWorkBitmap;

    private int[] mHolidays;

    private DisplayMetrics mDisplayMetrics;

    private Paint mPaint;
    private Paint mLunarPaint;

    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;

    private GestureDetector mGestureDetector;

    private int mcolumnSize, mRowSize, mSelectCircleSize;

    private OnWeekClickListener mOnWeekClickListener;
    private String mHolidayOrLunarText[];


    public WeekView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, DateTime dateTime) {
        this(context, array, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, DateTime dateTime) {
        this(context, array, attrs, 0, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttrs, DateTime dateTime) {
        super(context, attrs, defStyleAttrs);
        initAttrs(array, dateTime);
        initPaint();
        initWeek();
        initGestureDetector();


    }

    //주 뷰 기본 색상설정
    private void initAttrs(TypedArray array, DateTime dateTime) {
        Log.d(TAG, "array initAttrs -->" + array.toString());

        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_text_color, Color.parseColor("#FFFFFF")); //흰색
            mSelectBGColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_color, Color.parseColor("#E8E8E8")); //흰그레이
            mSelectBGTodayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_today_color, Color.parseColor("#FF8594")); //분홍
            mNormalDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_text_color, Color.parseColor("#575471")); //검남색
            mCurrentDayColor = array.getColor(R.styleable.WeekCalendarView_week_today_text_color, Color.parseColor("#FF8594")); //분홍
            mHintCircleColor = array.getColor(R.styleable.WeekCalendarView_week_hint_circle_color, Color.parseColor("#FE8595")); //분홍
            mLunarTextColor = array.getColor(R.styleable.WeekCalendarView_week_lunar_text_color, Color.parseColor("#ACA9BC")); //검남
            mHolidayTextColor = array.getColor(R.styleable.WeekCalendarView_week_holiday_color, Color.parseColor("#A68BFF")); //보라

            mDaySize = array.getInteger(R.styleable.WeekCalendarView_week_day_text_size, 13);
            mLunarTextSize = array.getInteger(R.styleable.WeekCalendarView_week_day_lunar_text_size, 8);
            mIsShowHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.WeekCalendarView_week_show_lunar, true);
            mIsShowHolidayHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_holiday_hint, true);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mSelectBGTodayColor = Color.parseColor("#FF8594");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLunarTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#A68BFF");
            mDaySize = 13;
            mDaySize = 8;
            mIsShowHint = true;
            mIsShowLunar = true;
            mIsShowHolidayHint = true;
        }
        mStartDate = dateTime; //startDate

        /**
         * 추후에 사용자가 휴일이든, 근무일자든 자기가 선택할 수 있도록 해야됨
         * 지금은 2018년, 2019년 휴일 국가공휴일만 지정해놓은상태.
         */

        int holidays[] = CalendarUtils.getInstance(getContext()).getHolidays(mStartDate.getYear(), mStartDate.getMonthOfYear());
        Log.d(TAG, "holiday start date -->" + mStartDate.getYear() +"--" + mStartDate.getMonthOfYear() + "--"+ mStartDate.getDayOfMonth());

        int row = CalendarUtils.getWeekRow(mStartDate.getYear(), mStartDate.getMonthOfYear() -1, mStartDate.getDayOfMonth());
        mHolidays = new int[7];

        //http://nan491.tistory.com/entry/Java-배열의-복사
        //holidays --> mHolidays
        System.arraycopy(holidays, row * 7, mHolidays, 0, mHolidays.length);

    }

    //WeekView paint
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics(); //size

        Log.d(TAG, "initPaint -->" + mDisplayMetrics.scaledDensity);
        //그리기 세팅
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        //음력 그리기
        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);
        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);

    }

    private void initWeek() {
        Calendar calendar = Calendar.getInstance();

        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);

        Log.d(TAG, "initWeek --->" + mCurrYear + "/" + mCurrMonth + "/" + mCurrDay);

        DateTime endDate = mStartDate.plusDays(7); //+7 days

        Log.d(TAG, "mStartDate.getMillis -->" + mStartDate.getMillis());
        Log.d(TAG, "endDate.getMillis -->" +endDate.getMillis());

        /**
         * 선택 연월 세팅
         * @mStartDate
         * @System.currentTimeMillis
         * @endDate
         */
        if (mStartDate.getMillis() <= System.currentTimeMillis() && endDate.getMillis() > System.currentTimeMillis()) {

            if (mStartDate.getMonthOfYear() != endDate.getMonthOfYear()) {

                if (mCurrDay < mStartDate.getDayOfMonth()) {
                    setSelectYearMonth(mStartDate.getYear(), endDate.getMonthOfYear() -1 , mCurrDay);
                } else {
                    setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() -1 , mCurrDay);
                }
            } else {
                setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() -1, mCurrDay);
            }
        } else {
            setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() -1, mStartDate.getMonthOfYear());
        }

//        initTaskHint(mStartDate);
//        initTaskHint(endDate);

    }

    //터치 이벤트 설정
    private void initGestureDetector() {

        //http://javaexpert.tistory.com/268
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            //살짝 터치
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    //그리기
    @Override
    protected void onDraw(Canvas canvas) {
        initSize();

        clearData();

        int selected = drawThisWeek(canvas);
        drawHintCircle(canvas);
    }


    /**
     * 힌트 draw
     *
     * @param canvas
     */
    private void drawHintCircle(Canvas canvas) {
        if (mIsShowHint) {
            mPaint.setColor(mHintCircleColor);
            int startMonth = mStartDate.getMonthOfYear();
            int endMonth = mStartDate.plusDays(7).getMonthOfYear();
            int startDay = mStartDate.getDayOfMonth();
            if (startMonth == endMonth) {
                List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
                for (int i = 0; i < 7; i++) {
                    drawHintCircle(hints, startDay + i, i, canvas);
                }
            } else {
                for (int i = 0; i < 7; i++) {
                    List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
                    List<Integer> nextHints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear());
                    DateTime date = mStartDate.plusDays(i);
                    int month = date.getMonthOfYear();
                    if (month == startMonth) {
                        drawHintCircle(hints, date.getDayOfMonth(), i, canvas);
                    } else {
                        drawHintCircle(nextHints, date.getDayOfMonth(), i, canvas);
                    }
                }
            }
        }
    }

    private void drawHintCircle(List<Integer> hints, int day, int col, Canvas canvas) {
        if (!hints.contains(day)) return;
        float circleX = (float) (mcolumnSize * col + mcolumnSize * 0.5);
        float circleY = (float) (mRowSize * 0.75);
        canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
    }

    //초반 사이즈
    private void initSize() {
        mcolumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight();
        mSelectCircleSize = (int) (mcolumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }

    }

    private void clearData() {
        mHolidayOrLunarText = new String[7];
    }

    //이번 Week 그리기
    private int drawThisWeek(Canvas canvas) {
        int selected = 0;
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            int day = date.getDayOfMonth();
            String dayString = String.valueOf(day);
            int startX = (int) (mcolumnSize * i + (mcolumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (day == mSelDay) {
                int startRecX = mcolumnSize * i;
                int endRecX = startRecX + mcolumnSize;
                if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, mPaint);
            }
            if (day == mSelDay) {
                selected = i;
                mPaint.setColor(mSelectDayColor);
            } else if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay && day != mSelDay && mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
            } else {
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[i] = CalendarUtils.getHolidayFromSolar(date.getYear(), date.getMonthOfYear() - 1, day);
        }
        return selected;
    }






    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }
    //클릭시 좌표
    private void doClickAction(int x, int y) {
        if (y > getHeight()) {
            return;
        }

        int column = x / mcolumnSize;
        column = Math.min(column, 6);

        DateTime date = mStartDate.plus(column);
        clickThisWeek(date.getYear(), date.getMonthOfYear() -1 ,date.getDayOfMonth());
    }

    //클릭한 주 설정
    public void clickThisWeek(int year, int month, int day) {
        if (mOnWeekClickListener !=  null) {
            mOnWeekClickListener.onClickDate(year, month, day);
        }

        setSelectYearMonth(year, month, day);
        invalidate();
    }

    //Week ClickListener
    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        mOnWeekClickListener = onWeekClickListener;
    }


   /* //데이터베이스에서 도트 프롬프트 데이터 가져 오기
    private void initTaskHint(DateTime date) {
        if (mIsShowHint) {
            ScheduleDB db  = ScheduleDB.getInstance(getContext());
            if (CalendarUtils.getInstance(getContext()).getTaskHints(date.getYear(), date.getMonthOfYear() -1).size() == 0)
                CalendarUtils.getInstance(getContext()).addTaskHints(date.getYear(), date.getMonthOfYear() -1, db.getTaskHintByMonth(mSelYear, mSelMonth));

        }
    }*/

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    public DateTime getStartDate() {
        return mStartDate;
    }

    public DateTime getEndDate() {
        return mStartDate.plusDays(6);
    }

    //선택 연월일
    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    /**
     * 현재 선택연도 가져오기
     * @return
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 현재 선택달 가져오기
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 현재 선택일 가져오기
     */
    public int getSelectDay() {
        return this.mSelDay;
    }
}
