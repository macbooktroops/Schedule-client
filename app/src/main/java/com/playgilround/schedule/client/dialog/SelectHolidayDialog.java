package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.realm.EventSetR;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 18-09-23
 * 몇년도 공휴일을 보고싶은지 물어보는 다이얼로그
 * 현재년도 +-1 만 가능.
 */
public class SelectHolidayDialog extends Dialog implements View.OnClickListener {

    static final String TAG = SelectHolidayDialog.class.getSimpleName();

    public Context mContext;

    private OnHolidaySetListener mOnHolidaySetListener;

    private TextView past, current, future, result;
    int nYear;

    int resultYear;
    EventSetR eventSetR;

    private boolean isChecked;
    public SelectHolidayDialog(Context context, OnHolidaySetListener onHolidaySetListener, EventSetR eventSet) {
        super(context, R.style.DialogFullScreen);
        mOnHolidaySetListener = onHolidaySetListener;
        mContext = context;
        eventSetR = eventSet;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_select_holiday);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        findViewById(R.id.pastYear).setOnClickListener(this);
        findViewById(R.id.currentYear).setOnClickListener(this);
        findViewById(R.id.futureYear).setOnClickListener(this);

        past = (TextView) findViewById(R.id.pastYear);
        current = (TextView) findViewById(R.id.currentYear);
        future = (TextView) findViewById(R.id.futureYear);

        result = (TextView) findViewById(R.id.resultText);

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        nYear = calendar.get(Calendar.YEAR);

        past.setText(nYear -1+"년");
        current.setText(nYear +"년");
        future.setText(nYear +1 +"년");



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                if (isChecked) {
                    if (mOnHolidaySetListener != null) {
                        mOnHolidaySetListener.onHolidaySet(resultYear, eventSetR);
                    }
                    dismiss();

                } else {
                    Toast.makeText(getContext(), "연도를 클릭해주세요.",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.pastYear:
                Log.d(TAG, "pastClick");
                result.setText(nYear -1+"년 공휴일을 보시겠어요?");
                resultYear = nYear -1;
                isChecked = true;
                break;
            case R.id.currentYear:
                Log.d(TAG, "currentClick");
                result.setText(nYear+"년 공휴일을 보시겠어요?");
                resultYear = nYear;
                isChecked = true;
                break;
            case R.id.futureYear:
                Log.d(TAG, "futureClick");
                result.setText(nYear +1+"년 공휴일을 보시겠어요?");
                resultYear = nYear + 1;
                isChecked = true;
                break;
        }
    }

    public interface OnHolidaySetListener {
        void onHolidaySet(int year, EventSetR eventSet);
    }
}
