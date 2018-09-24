package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-09-23
 * 몇년도 공휴일을 보고싶은지 물어보는 다이얼로그
 * 현재년도 +-1 만 가능.
 */
public class SelectHolidayDialog extends Dialog implements View.OnClickListener {

    static final String TAG = SelectHolidayDialog.class.getSimpleName();

    private int mYear;
    private OnHolidaySetListener mOnHolidaySetListener;

    public SelectHolidayDialog(Context context, OnHolidaySetListener onHolidaySetListener) {
        super(context, R.style.DialogFullScreen);
        mOnHolidaySetListener = onHolidaySetListener;

        setContentView(R.layout.dialog_select_holiday);
    }

    private void initView() {
        setContentView(R.layout.dialog_select_holiday);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                if (mOnHolidaySetListener != null) {
                    mOnHolidaySetListener.onHolidaySet(mYear);
                }
        }
    }

    public interface OnHolidaySetListener {
        void onHolidaySet(int year);
    }
}
