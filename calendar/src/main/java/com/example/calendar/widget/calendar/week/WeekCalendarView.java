package com.example.calendar.widget.calendar.week;

import android.support.v4.view.ViewPager;

import com.example.calendar.widget.calendar.OnCalendarClickListener;

/**
 * 18-06-01
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;
}
