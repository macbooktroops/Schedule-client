package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.LoginActivity;

/**
 * 18-10-31
 * 도착 랭킹 화면에서,
 * 유저가 도착 시간을 보고싶을 경우 표시되는 다이얼로
 */
public class ArrivedRankDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ArrivedRankDialog.class.getSimpleName();

    String retName;
    String retArrivedAt;

    public Context mContext;

    public ArrivedRankDialog(Context context, String name, String arrivedAt) {
        super(context, R.style.DialogFullScreen);

        mContext = context;
        retName = name;
        retArrivedAt = arrivedAt;


        Log.d(TAG, "rankDialog ->" + retName + "--" + retArrivedAt);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_arrived_detail);

        TextView title = findViewById(R.id.tvTitle);
        title.setText(retName + "님의 도착시간");

        TextView tvEmail = findViewById(R.id.tvArrivedAt);
        tvEmail.setText(retArrivedAt);

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                dismiss();
                break;
        }
    }
}
