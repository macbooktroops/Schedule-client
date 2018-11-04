package com.playgilround.schedule.client.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.calendar.OnCalendarClickListener;

/**
 * 18-05-26
 * 월 캘린더
 * //좌우 슬라이더 ViewPager 상속
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    static final String TAG = MonthCalendarView.class.getSimpleName();
    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

//    /**
//     * Attrs 속성 초기화
//     * @param context
//     * @param attrs

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    /**
     * Month Adapter
     * @param context
     * @param array
     */
    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2 , false); //특정 페이지로 이동
    }

    //이번 달 클릭 시..
    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    //지난 달 클릭 시
    @Override
    public void onClickLastMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() -1); //현재 아이템페이지에서 -1
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }

        setCurrentItem(getCurrentItem() -1, true);
    }
    //다음 달 클릭 시
    @Override
    public void onClickNextMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() +1); //current 페이지에서 +1
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
            Log.d(TAG, "onCLickLastMonth ---> " +year + "/" + month + "/" + day);

            monthDateView.invalidate();
        }

        onClickThisMonth(year, month, day);
        setCurrentItem(getCurrentItem() + 1, true); //현재 아이템 포지션
    }

    /**
     * http://kingorihouse.tumblr.com/post/87079690019/android-viewpageronpagechangelistener-의-이벤트-전달-순서
     * ViewPager 상태 변화 리스너
     */
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        //페이지 터치 스크롤
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.d(TAG, "onPageScrolled");
        }

        //새 페이지 선택
        @Override
        public void onPageSelected(final int position) {
//            Log.d(TAG, "onPageSelected");
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());

            if (monthView != null) {
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());

                if (mOnCalendarClickListener != null) {
                    //페이지 체인지
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
                }
            } else {
                MonthCalendarView.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(position);
                    }
                }, 50);
            }
        }

        /**
         * 스크롤 상태가 변경되면 호출됩니다.
         * 사용자가 드래그를 시작할 때, 호출기가 현재 페이지로 자동으로 이동하거나,
         * 완전히 중지 / 유휴 상태 일 때 검색하는 데 유용합니다.
         * 변경된 페이지Position을 CallBack받을 수 있다.
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged");
        }
    };


    public SparseArray<MonthView> getMonthViews() {
        Log.d(TAG, "getMonthViews --> " + mMonthAdapter.getViews());
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

    /**
     * 클릭 날짜 리스너
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

}
