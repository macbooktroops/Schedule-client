package com.playgilround.calendar.widget.calendar.schedule;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 18-05-26
 * 스케줄 화면 스크롤, 터치 클릭 리스너 클래스
 * : extends하여 모든 제스쳐들은 좀더 다양한 이벤트로 감지 할 수 있다.
 */
public class OnScheduleScrollListener extends GestureDetector.SimpleOnGestureListener {


    private ScheduleLayout mScheduleLayout;

    public OnScheduleScrollListener(ScheduleLayout scheduleLayout) {
        mScheduleLayout = scheduleLayout;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mScheduleLayout.onCalendarScroll(distanceY);
        return true;
    }


}
