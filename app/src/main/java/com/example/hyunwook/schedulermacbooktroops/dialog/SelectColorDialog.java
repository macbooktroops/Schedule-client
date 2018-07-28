package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-07-06
 * 스케줄 분류에 색깔 선택 다이얼로그
 * 추후에 중요한 순서대로 색깔 가능하도록 설정예정
 */
public class SelectColorDialog extends Dialog implements View.OnClickListener {

    static final String TAG = SelectColorDialog.class.getSimpleName();
    private OnSelectColorListener mOnSelectColorListener;

    private int mColor;

    private View[] mColorBorderView;
    private View[] mColorView;
    public SelectColorDialog(Context context, OnSelectColorListener onSelectColorListener) {
        super(context, R.style.DialogFullScreen);
        mOnSelectColorListener = onSelectColorListener;

        setContentView(R.layout.dialog_select_color);
        initView();
    }

    private void initView() {
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        mColorBorderView = new View[6]; //6개
        mColorView = new View[6];

        LinearLayout llTopColor = (LinearLayout) findViewById(R.id.llTopColor); //상단 색깔 레이아웃
        LinearLayout llBottomColor = (LinearLayout) findViewById(R.id.llBottomColor); //하단 색깔 레이아웃

        for (int i = 0; i< llTopColor.getChildCount(); i++ ) { //자식뷰 개수만큼
            RelativeLayout child = (RelativeLayout) llTopColor.getChildAt(i); //위치

            mColorBorderView[i] = child.getChildAt(0); //자식뷰에 첫번째가 테두리.
            mColorView[i] = child.getChildAt(1);

            final int finalI = i;
            //해당 칸 클릭 시
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeColor(finalI);
                }
            });
        }
        for (int i = 0; i < llBottomColor.getChildCount(); i++ ) {
            RelativeLayout child = (RelativeLayout) llBottomColor.getChildAt(i);

            mColorBorderView[i + 3] = child.getChildAt(0);
            mColorView[i + 3] = child.getChildAt(1);

            final int finalI = i;
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeColor(finalI + 3);
                }
            });
        }
    }


    //선택된 칸은 테두리 뷰 형태.
    private void changeColor(int position) {
        mColor = position;
        for (int i = 0; i < mColorBorderView.length; i++) {
            mColorBorderView[i].setVisibility(position == i ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                if (mOnSelectColorListener != null) {
                    Log.d(TAG, "mColor -->" + mColor);
                    mOnSelectColorListener.onSelectColor(mColor);
                }

                dismiss();
                break;
        }
    }


    public interface OnSelectColorListener {
        void onSelectColor(int color);
    }
}
