package com.playgilround.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.playgilround.calendar.R;

import org.joda.time.DateTime;

/**
 * 18-05-26
 * ViewPager 내부의 페이지를 채우기위한 어댑터를 제공하는 기본 클래스.
 */
public class MonthAdapter extends PagerAdapter {

    static final String TAG = MonthAdapter.class.getSimpleName();
    private Context mContext;
    private TypedArray mArray;
    private MonthCalendarView mMonthCalendarView;
    private int mMonthCount;

    //http://developer88.tistory.com/91 SparseArry
    private SparseArray<MonthView> mViews;

    public MonthAdapter(Context context, TypedArray array, MonthCalendarView monthCalendarView) {
        mContext = context;
        mArray = array;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();
        mMonthCount = array.getInteger(R.styleable.MonthCalendarView_month_count, 48);

    }

    @Override
    public int getCount() {
        return mMonthCount;
    }

    //ViewPager의 getCount()에서 얻어온 Count의
    // Position별로 Pager에 등록할 item을 처리
    //instantiateItem : ViewPager에 사용할 View 를 생성하고 등록 합니다 .
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            int date[] = getYearAndMonth(position);
            Log.d(TAG, "date --> " + date);

            MonthView monthView = new MonthView(mContext, mArray, date[0], date[1]);
            monthView.setId(position);
            monthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            monthView.invalidate();
            monthView.setOnDateClickListener(mMonthCalendarView);
            mViews.put(position, monthView);
        }

        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    /**
     * https://www.lesstif.com/display/JAVA/Joda-Time
     * @param position
     * @return
     */
    private int[] getYearAndMonth(int position) {
        int date[] = new int[2];
        DateTime time = new DateTime();
        Log.d(TAG, "getYearMonth -->" + position + "--" + mMonthCount);
        time = time.plusMonths(position - mMonthCount / 2);

        date[0] = time.getYear(); //year
        date[1] = time.getMonthOfYear() -1 ; //month
        return date;
    }

    //화면에서 사라진 View 를 삭제 합니다.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //instantiateItem 에서 생성한 객체를 사용할지 여부를 판단
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }

    public int getMonthCount() {
        return mMonthCount;
    }
}
