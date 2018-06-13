package com.example.calendar.widget.calendar.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 18-06-06
 * 스케줄 하단 RecyclerView
 */
public class ScheduleRecyclerView extends RecyclerView {

    public ScheduleRecyclerView(Context context) {
        this(context, null);
    }

    public ScheduleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isScrollTop() {
        /**
         * RecyclerView 영역중에서 어느 정도 움직였는지 - 값으로 넘겨 준다.
         * -값으로 넘겨주는 이유는 Scroll이 아래로 움직였기 때문이다
         * 그래서 얼마나 움직였는지 확인 하고자 할 경우 -1을 곱해주면 된다.
         */
        return computeVerticalScrollOffset() == 0;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);

        }
    }
}
