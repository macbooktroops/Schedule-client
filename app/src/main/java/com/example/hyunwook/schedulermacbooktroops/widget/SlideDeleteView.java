package com.example.hyunwook.schedulermacbooktroops.widget;

import android.content.Context;
import android.transition.Slide;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 18-07-07
 * 좌측메뉴 스케줄 항목 슬라이드 시 삭제 뷰
 */
public class SlideDeleteView extends FrameLayout {

    private float downX, moveX;

    private long startTime, endTime;

    private boolean mIsMove = false;
    public SlideDeleteView(Context context) {
        this(context, null);
    }

    public SlideDeleteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * xml 로 부터 모든 뷰를 inflate 를 끝내고 실행된다.
     * 대부분 이 함수에서는 각종 변수 초기화가 이루어 진다.
     * super 메소드에서는 아무것도 하지않기때문에 쓰지 않는다.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) { //멀티터치는  getActionMasked

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                moveX = downX;

                startTime = System.currentTimeMillis();

                mIsMove = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - moveX) >= 5) {
                    moveChild(event.getX() - moveX);
                    moveX = event.getX();
                    mIsMove = true;
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endTime = System.currentTimeMillis();
                moveX = event.getX();
                determineSpeed();
                isClickContentView(event);
                return true;
                }
    }
}
