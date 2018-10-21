package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 18-10-21
 * 스케줄 푸쉬 받을 때
 * 스케줄 공유 수락, 거부 요청 다이얼로그
 */
public class ScheduleAssentDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ScheduleAssentDialog.class.getSimpleName();

    public Context mContext;

    private OnScheduleAssentSet mOnScheduleAssentSet;

    TextView tvAssent;

    public ScheduleAssentDialog(Context context, OnScheduleAssentSet onScheduleAssentSet) {
        super(context, R.style.DialogFullScreen);

        mOnScheduleAssentSet = onScheduleAssentSet;

        mContext = context;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_schedule_assent);

        tvAssent = findViewById(R.id.tvAssent);
//        tvAssent.setText();

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNegative:
                dismiss();
                break;
            case R.id.tvPositive:
                dismiss();
        }
    }

    public interface OnScheduleAssentSet {
        void onScheAssent();
    }
}
