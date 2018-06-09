package com.example.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.*;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.example.calendar.R;

import java.util.jar.Attributes;

/**
 * 18-05-26
 * 월 캘린더
 * //좌우 슬라이더 ViewPager 상속
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMon
    }
}
