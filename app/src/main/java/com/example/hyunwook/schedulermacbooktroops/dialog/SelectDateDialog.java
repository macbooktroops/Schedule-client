package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.example.calendar.widget.calendar.OnCalendarClickListener;
import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-06-11
 * 이미지 시계 버튼 클릭 시
 * 스케줄 입력 전 시작 시간을 정할 수있는 다이얼로그
 */
public class SelectDateDialog extends Dialog implements View.OnClickListener, OnCalendarClickListener {

    private OnSelectDateListener mOnSelectDateListener;
    public SelectDateDialog(Context context, OnSelectDateListener onSelectDateListener,
                            int year, int month, int day, int position) {

        super(context, R.style.DialogFullScreen);

        mOnSelectDateListener = onSelectDateListener;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_select_date);
    }

        //스케줄 날짜 클릭
        public interface OnSelectDateListener {
            void onSelectDate(int year, int month, int day, long time, int position);
        }
}

