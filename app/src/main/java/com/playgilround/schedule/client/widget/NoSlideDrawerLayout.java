package com.playgilround.schedule.client.widget;

/**
 * 18-05-23
 * DrawableLayout : 평상시에는 숨겨있다가 스와이프하면 표시되는레이아웃.
 */

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.jar.Attributes;

public class NoSlideDrawerLayout extends DrawerLayout {
    static final String TAG = NoSlideDrawerLayout.class.getSimpleName();
    private View vMenu;
    private boolean mCanMove;

    //커스텀 뷰 생성
    public NoSlideDrawerLayout(Context context) {
        super(context);
    }

    public NoSlideDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSlideDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * xml 로 부터 모든 뷰를 inflate 를 끝내고 실행.
     * 대부분 이 함수에서는 각종 변수 초기화가 이루어짐.
     * super 메소드에서는 아무것도하지않기 때문에 쓰지 않는다.
     */

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        vMenu = findViewWithTag("menu"); //menu 태그
    }

    //하위 뷰에게 이벤트전달
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "ACTION DOWN");
            int width = vMenu.getWidth();

            //slide가 넘는곳이 터치되거나, 그안이 터치될경우.
            mCanMove = ev.getX() >= width || ev.getX() < 15;
            Log.d(TAG, "width --> " +width);
            Log.d(TAG, "ev.getx --> " +ev.getX());
        }

        try {
            return mCanMove && super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }
}