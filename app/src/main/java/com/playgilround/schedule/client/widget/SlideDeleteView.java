package com.playgilround.schedule.client.widget;

import android.content.Context;
import android.transition.Slide;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.playgilround.calendar.widget.calendar.schedule.AutoMoveAnimation;

/**
 * 18-07-07
 * 좌측메뉴 스케줄 항목 슬라이드 시 삭제 뷰
 */
public class SlideDeleteView extends FrameLayout {

    private float downX, moveX;

    private int mWidth;
    private int mDeleteViewWidth;

    private View vDeleteView;
    private long startTime, endTime;

    private boolean mIsMove = false;
    private boolean mIsOpen = false;

    private OnContentClickListener mOnContentClickListener;
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
        return super.onTouchEvent(event);
    }

    //클릭
    private void isClickContentView(MotionEvent event) {
        if (!mIsMove && Math.abs(event.getX() - downX) < 5 &&
                mOnContentClickListener != null) {
            if (mIsOpen) {
                close(true);
            } else {
                mOnContentClickListener.onContentClick();
            }
        }
        downX = 0;
        moveX = 0;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right;

        for (int i = 0; i <getChildCount(); i++) {
            View child = getChildAt(i);

            if ("delete".equals(child.getTag())) {
                vDeleteView = child;
                mDeleteViewWidth = child.getWidth();
                child.layout(right, 0, right + mDeleteViewWidth, child.getHeight());
            }
        }
    }

    //닫기
    public void close(boolean anim) {
        if (isOpen()) {
            mIsOpen = false;

            if (anim) {
                //닫히는 애니메이션
                vDeleteView.startAnimation(new AutoMoveAnimation((long) (mWidth - vDeleteView.getX())));
            } else {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.setX(child.getX() + mDeleteViewWidth);
                }
            }
        }
    }
    //움직일때.
    private void moveChild(float distanceX) {
        if (vDeleteView != null) {
            if (vDeleteView.getX() >= mWidth - mDeleteViewWidth &&
                    vDeleteView.getX() <= mWidth) {
                distanceX *= 1.5;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);

                    float positionX = child.getX() + distanceX;

                    if (child != vDeleteView) {
                        positionX = Math.max(positionX, -mDeleteViewWidth);
                        positionX = Math.min(positionX, 0);
                    } else {
                        positionX = Math.max(positionX, mWidth - mDeleteViewWidth);
                        positionX = Math.min(positionX, mWidth);
                    }
                    child.setX(positionX);
                }
            }
        }
    }

    //슬라이드가 오픈됬나.
    private boolean isOpen() {
        return mIsOpen;
    }

    //취소
    private void determineSpeed() {
        if ((moveX - downX) / (endTime - startTime) < -0.5) {
            mIsOpen = true;
            vDeleteView.startAnimation(new AutoMoveAnimation((long) (mWidth - mDeleteViewWidth / 2 - vDeleteView.getX())));
        } else if ((moveX - downX) / (endTime - startTime) > 0.5) {
            mIsOpen = false;
            vDeleteView.startAnimation(new AutoMoveAnimation((long) (mWidth - vDeleteView.getX())));
        } else {
            determineTheState();
        }
    }

    private void determineTheState() {
        if (vDeleteView.getX() < mWidth - mDeleteViewWidth / 2) { //open
            mIsOpen = true;
            if (vDeleteView.getX() != mWidth - mDeleteViewWidth) {
                vDeleteView.startAnimation(new AutoMoveAnimation((long) (mWidth - mDeleteViewWidth / 2 - vDeleteView.getX())));
            }
        } else { //close
            mIsOpen = false;
            if (vDeleteView.getX() != mWidth) {
                vDeleteView.startAnimation(new AutoMoveAnimation((long) (mWidth - vDeleteView.getX())));
            }
        }
    }


    private class AutoMoveAnimation extends Animation {

        private AutoMoveAnimation(long duration) {
            setDuration(Math.max(duration * 10, mDeleteViewWidth / 5));
        }

        //실제 처리
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            if (isOpen()) {
                moveChild(-mDeleteViewWidth / 10);
            } else {
                moveChild(mDeleteViewWidth / 10);
            }
        }
    }


    public void setOnContentClickListener(OnContentClickListener onContentClickListener) {
        mOnContentClickListener = onContentClickListener;
    }

    public interface OnContentClickListener {
        void onContentClick();
    }
}
