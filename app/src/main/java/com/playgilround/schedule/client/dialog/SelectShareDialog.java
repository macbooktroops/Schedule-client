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
 * 18-10-28
 * 몇년도 공유된 일정을 볼껀지 요청받는 다이얼로그
 * 현재년도 +-1
 */
public class SelectShareDialog extends Dialog implements View.OnClickListener {

    static final String TAG = SelectShareDialog.class.getSimpleName();

    public Context mContext;

    private OnShareSetListener mOnShareSetListener;

    private TextView past, current, future, result, share;

    int nYear;

    int resultYear;
    EventSetR eventSetR;

    private boolean isChecked;

    public SelectShareDialog(Context context, OnShareSetListener onShareSetListener, EventSetR eventSet) {
        super(context, R.style.DialogFullScreen);
        mOnShareSetListener = onShareSetListener;
        mContext = context;
        eventSetR = eventSet;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_select_share);

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        findViewById(R.id.pastYear).setOnClickListener(this);
        findViewById(R.id.currentYear).setOnClickListener(this);
        findViewById(R.id.futureYear).setOnClickListener(this);

        past =  findViewById(R.id.pastYear);
        current = findViewById(R.id.currentYear);
        future = findViewById(R.id.futureYear);

        result = findViewById(R.id.resultText);

        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        nYear = calendar.get(Calendar.YEAR);

        past.setText(nYear -1+"년");
        current.setText(nYear +"년");
        future.setText(nYear +1 +"년");

        share = findViewById(R.id.tvShare);
        share.setText("공유된 스케줄을 \n 보고싶은 연도를 선택해주세요");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                if (isChecked) {
                    if (mOnShareSetListener != null) {
                        mOnShareSetListener.onShareSet(resultYear, eventSetR);
                    }
                    dismiss();

                } else {
                    Toast.makeText(getContext(), "연도를 클릭해주세요.",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.pastYear:
                Log.d(TAG, "pastClick");
                result.setText(nYear -1+"년 스케줄을 보시겠어요?");
                resultYear = nYear -1;
                isChecked = true;
                break;
            case R.id.currentYear:
                Log.d(TAG, "currentClick");
                result.setText(nYear+"년 스케줄을 보시겠어요?");
                resultYear = nYear;
                isChecked = true;
                break;
            case R.id.futureYear:
                Log.d(TAG, "futureClick");
                result.setText(nYear +1+"년 스케줄을 보시겠어요?");
                resultYear = nYear + 1;
                isChecked = true;
                break;
        }
    }

    public interface OnShareSetListener {
        void onShareSet(int year, EventSetR eventSetR);
    }
}
