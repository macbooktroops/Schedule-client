package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 18-10-21
 * 스케줄 푸쉬 받을 때
 * 스케줄 공유 수락, 거부 요청 다이얼로그
 */
public class ScheduleAssentDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ScheduleAssentDialog.class.getSimpleName();

    public Context mContext;

    private OnScheduleAssentSet mOnScheduleAssentSet;

    TextView tvAssent, tvResult;
    String retName, retTitle;

    public ScheduleAssentDialog(Context context, OnScheduleAssentSet onScheduleAssentSet, String name, String title) {
        super(context, R.style.DialogFullScreen);

        mOnScheduleAssentSet = onScheduleAssentSet;

        mContext = context;
        retName = name;
        retTitle = title;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_schedule_assent);

        tvAssent = findViewById(R.id.tvAssent);
        tvAssent.setText(retName +" 님에 " + "'"+ retTitle +"'");

        tvResult = findViewById(R.id.resultText);
        tvResult.setText("이 스케줄을 앞으로 \n 공유하시겠어요?");
        tvResult.setGravity(Gravity.CENTER);

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvNegative:
                if (mOnScheduleAssentSet != null) {
                    mOnScheduleAssentSet.onScheAssent(false);
                }
                dismiss();
                break;
            case R.id.tvPositive:
                if (mOnScheduleAssentSet != null) {
                    mOnScheduleAssentSet.onScheAssent(true);
                }
                dismiss();
        }
    }

    public interface OnScheduleAssentSet {
        void onScheAssent(boolean state);
    }
}
