package com.example.calendar.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.example.calendar.R;

/**
 * 18-05-25
 * 메인에 현재 날짜 표시해주는 뷰
 */
public class WeekBarView extends View {

    static final String TAG = WeekBarView.class.getSimpleName();
    private int mWeekTextColor;
    private int mWeekSize;
    private String[] mWeekString;

    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;


    public WeekBarView(Context context) {
        this(context, null);
    }

    public WeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();

    }


    //주간 날짜 특징 설정
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WeekBarView);

        //날짜 표시 색깔
        mWeekTextColor = array.getColor(R.styleable.WeekBarView_week_text_color, Color.parseColor("#4588E3"));

        mWeekSize = array.getInteger(R.styleable.WeekBarView_week_text_size, 13);
        mWeekString = context.getResources().getStringArray(R.array.calendar_week); //Sun~Saturday

        array.recycle();

    }

    //그리기
    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics(); //현재 디스플레이
        mPaint = new Paint();
        mPaint.setColor(mWeekTextColor);
        mPaint.setAntiAlias(true);
        Log.d(TAG, "scale -->" + mDisplayMetrics.scaledDensity);
        mPaint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
    }

    //화면 사이즈 구하기
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 30;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int columnWidth = width / 7;

        for (int i =0; i < mWeekString.length; i++) {
            String text = mWeekString[i];
            Log.d(TAG, "text onDraw --> " +text);
            int fontWidth = (int) mPaint.measureText(text);
            int startX = columnWidth * i + (columnWidth - fontWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(text, startX, startY, mPaint);
        }
    }
}
