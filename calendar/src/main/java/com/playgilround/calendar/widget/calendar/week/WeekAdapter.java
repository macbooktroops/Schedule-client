package com.playgilround.calendar.widget.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.calendar.R;
import com.playgilround.calendar.widget.calendar.WeekBarView;

import org.joda.time.DateTime;

/**
 * 18-06-02
 *  ViewPager 내부의 페이지를 채우기위한 어댑터를 제공하는 기본 클래스
 */
public class WeekAdapter extends PagerAdapter {

    static final String TAG = WeekAdapter.class.getSimpleName();
    private SparseArray<WeekView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private WeekCalendarView mWeekCalendarView;

    private DateTime mStartDate;
    private int mWeekCount = 220;

    public WeekAdapter(Context context, TypedArray array, WeekCalendarView weekCalendarView) {
        mContext = context;
        mArray = array;
        mWeekCalendarView = weekCalendarView;
        mViews = new SparseArray<>();
        initStartDate();

        mWeekCount = array.getInteger(R.styleable.WeekCalendarView_week_count, 220);
    }

    private void initStartDate() {
        mStartDate = new DateTime();
        Log.d(TAG, -mStartDate.getDayOfWeek() + "initStartDate");
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() & 7);
    }

    @Override
    public int getCount() {
        return mWeekCount;
    }
    //ViewPager의 getCount()에서 얻어온 Count의
    // Position별로 Pager에 등록할 item을 처리
    //instantiateItem : ViewPager에 사용할 View 를 생성하고 등록 합니다 .
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        for (int i = 0; i< 3; i++) {
            Log.d(TAG, "instantiateItem position ->" + position);
            if (position -2 + i >= 0 && position -2 + i < mWeekCount && mViews.get(position -2 + i) == null) {
                instanceWeekView(position -2 + i);
            }
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    /**
     *  instantiateItem 에서 생성한 객체를 사용할지 여부를 판단 합니다 .
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //destroyItem : 화면에서 사라진 View 를 삭제 합니다
   @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
   }

    public SparseArray<WeekView> getViews() {
        return mViews;
    }

    public int getWeekCount() {
        return mWeekCount;
    }

    public WeekView instanceWeekView(int position) {
        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(position - mWeekCount /2));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setOnWeekClickListener(mWeekCalendarView);
        weekView.invalidate();
        mViews.put(position, weekView);
        return weekView;
    }
}
