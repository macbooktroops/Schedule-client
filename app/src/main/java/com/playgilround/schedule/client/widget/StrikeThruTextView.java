package com.playgilround.schedule.client.widget;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 18-06-17
 *
 */
public class StrikeThruTextView extends TextView {

    public StrikeThruTextView(Context context) {
        this(context, null);
    }

    public StrikeThruTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrikeThruTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    /**
     * 그리기 세팅
     *TextPaint :텍스트 측정 및 그리기 중에 사용되는
     * 일부 추가 데이터를위한 공간을 남겨 두는 Paint의 확장입니다.
     * http://www.masterqna.com/android/9496/textview-글씨에-삭선을-넣고-싶습니다
     */

    private void initPaint() {
        TextPaint paint = getPaint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

}
