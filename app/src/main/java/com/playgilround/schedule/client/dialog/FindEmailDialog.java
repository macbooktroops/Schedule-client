package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.LoginActivity;

/**
 * 18-10-07
 * 이메일 찾기가 완료되고 결과 다이얼로그
 */
public class FindEmailDialog extends Dialog implements View.OnClickListener {

    static final String TAG = FindEmailDialog.class.getSimpleName();

    private OnFindEmailListener mOnFindEmailListener;

    public Context mContext;
    String retEmail; //찾은 이메일 결과 값
    String retName; //찾은 닉네임 결과

    public FindEmailDialog(Context context, String name, String email) {
        super(context, R.style.DialogFullScreen);
//        mOnFindEmailListener = onFindEmailListener;

        mContext = context;

        retEmail = email;
        retName = name;

        Log.d(TAG, "FindEmail -->" + retEmail + "--" + retName);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_find_email);

        TextView title = findViewById(R.id.tvTitle);
        title.setText(retName + "님 이메일");

        TextView tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(retEmail);

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
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                dismiss();
        }
    }


    public interface OnFindEmailListener {
        void onEmailListener(String email);
    }
}
