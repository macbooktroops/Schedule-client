package com.example.calendar.widget.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

import com.example.calendar.R;
import com.example.calendar.widget.calendar.OnCalendarClickListener;

/**
 * 18-06-01
 * 일일 표시 작업 커스텀 뷰
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    static final String TAG = WeekCalendarView.class.getSimpleName();

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;


    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);

        //뷰페이져 내부에 존재하는 페이지의 변화
        //http://itpangpang.xyz/290
        addOnPageChangeListener(mOnPageChangeListener);
    }

    //Attrs setting
    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));

    }


    //Adapter 세팅
    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array, this);
        setAdapter(mWeekAdapter);

        Log.d(TAG, "initWeekAdapter -->" + mWeekAdapter.getWeekCount());
        setCurrentItem(mWeekAdapter.getWeekCount() / 2, false); //선택 페이지..
    }

    /**
     * http://kingorihouse.tumblr.com/post/87079690019/android-viewpageronpagechangelistener-의-이벤트-전달-순서
     */
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //페이지 스크롤
        }

        //새 페이지 선택
        @Override
        public void onPageSelected(final int position) {
            WeekView weekView = mWeekAdapter.getViews().get(position);
            Log.d(TAG, "onPageSelected -->" + weekView.getSelectYear() + "/" + weekView.getSelectMonth() + "/" + weekView.getSelectDay());
            if (weekView != null) {
                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(weekView.getSelectYear(), weekView.getSelectMonth(), weekView.getSelectDay());
                }
                weekView.clickThisWeek(weekView.getSelectYear(), weekView.getSelectMonth(), weekView.getSelectDay());
            } else {
                WeekCalendarView.this.postDelayed(new Runnable() {
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

        }
    };

    //날짜 클릭
    @Override
    public void onClickDate(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    /**
     * 클릭 날짜 리스너
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }


    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }
}

