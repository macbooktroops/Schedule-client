package com.example.calendar.widget.calendar.schedule;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.calendar.R;
import com.example.calendar.widget.calendar.month.MonthCalendarView;
import com.example.calendar.widget.calendar.month.MonthView;

import java.util.Calendar;
import java.util.jar.Attributes;

/**
 * 18-05-25
 * 스케줄 표시 레이아웃
 * 실질적 표시
 */
public class ScheduleLayout extends FrameLayout {

//    private MonthCalendarView monthView;
    static final String TAG = ScheduleLayout.class.getSimpleName();
    private final int DEFAULT_MONTH = 0;
    private final int DEFAULT_WEEK = 1;
    private int mDefaultView;
    private boolean mIsAutoChangeMonthRow;

    private MonthCalendarView mvView;

    private boolean mCurrentRowsIsSix = true;

    private int mRowSize;
    private int mMinDistance;
    private int mAutoScrollDistance;

    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;

    private MonthCalendarView monthCalendar;
    //터치 이벤트 처리
    private GestureDetector mGestureDetector;

    private RelativeLayout rlMonthCalendar;
    private RelativeLayout rlScheduleList;

    private ScheduleState mState;
    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context.obtainStyledAttributes(attrs, R.styleable.ScheduleLayout));
    }

    //스케줄 사이즈 설정
    public void initAttrs(TypedArray array) {
        mDefaultView = array.getInt(R.styleable.ScheduleLayout_default_view, DEFAULT_MONTH);
        mIsAutoChangeMonthRow = array.getBoolean(R.styleable.ScheduleLayout_auto_change_month_row, false);
        array.recycle();

        //스케줄 상태
        mState = ScheduleState.OPEN;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
        mMinDistance = getResources().getDimensionPixelSize(R.dimen.calendar_min_distance);
        mAutoScrollDistance = getResources().getDimensionPixelSize(R.dimen.auto_scroll_distance);

        initDate();
    }

    //Date 얻기
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        resetCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        initGestureDetector();
    }

    //현재 선택한 날짜 재설정
    private void resetCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear  = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;

        Log.d(TAG, "current day -->" + mCurrentSelectYear + "-" + mCurrentSelectMonth + "-" + mCurrentSelectDay);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnScheduleScrollListener(this));
    }


    /**
     * xml 로 부터 모든 뷰를 inflate 를 끝내고 실행.
     * 대부분 이 함수에서는 각종 변수 초기화가 이루어짐.
     * super 메소드에서는 아무것도하지않기 때문에 쓰지 않는다.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        monthCalendar = (MonthCalendarView) findViewById(R.id.m)
    }

    //캘린더 스크롤 시..
    protected void onCalendarScroll(float distanceY) {
        MonthView monthView = monthCalendar.getCurrentMonthView();
        Log.d(TAG, "mcView getCurrentMonthView --> " + monthView);

        distanceY = Math.min(distanceY, mAutoScrollDistance);
        float calendarDistanceY = distanceY / (mCurrentRowsIsSix ? 5.0f : 4.0f);

        int row = monthView.getWeekRow() -1;
        Log.d(TAG, "onCalendarScroll row -->" + row);

        int calendarTop = -row * mRowSize;

        int scheduleTop = mRowSize;

        float calendarY = rlMonthCalendar.getY() - calendarDistanceY * row;
        calendarY = Math.min(calendarY, 0);
        calendarY = Math.max(calendarY, calendarTop);
        rlMonthCalendar.setY(calendarY);

        float scheduleY = rlScheduleList.
    }
}
