package com.example.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;

/**
 * 18-05-26
 * ViewPager 내부의 페이지를 채우기위한 어댑터를 제공하는 기본 클래스.
 */
public class MonthAdapter extends PagerAdapter {

    private Context mContext;
    private TypedArray mArray;
    private MonthCalendarView mMonthCalendarView;

    //http://developer88.tistory.com/91 SparseArry
    private SparseArray<MonthView> mViews;

    public MonthAdapter(Context context, TypedArray array, MonthCalendarView monthCalendarView) {
        mContext = context;
        mArray = array;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();

    }
}
