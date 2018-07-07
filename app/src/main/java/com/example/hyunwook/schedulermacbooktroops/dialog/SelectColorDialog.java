package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-07-06
 * 스케줄 분류에 색깔 선택 다이얼로그
 * 추후에 중요한 순서대로 색깔 가능하도록 설정예정
 */
public class SelectColorDialog extends Dialog implements View.OnClickListener {

    private OnSelectColorListener mOnSelectColorListener;

    public SelectColorDialog(Context context, OnSelectColorListener onSelectColorListener) {
        super(context, R.style.DialogFullScreen);
        mOnSelectColorListener = onSelectColorListener;

        setContentView(R.layout.dialog_select_color);
    }


    public interface OnSelectColorListener {
        void onSelectColor(int color);
    }
}
