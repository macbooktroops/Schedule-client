package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.playgilround.schedule.client.R;

/**
 * 18-10-08
 * 패스워드 토큰을 얻은 후에,
 * 변경할 패스워드 입력 다이얼로그
 */
public class ResetPasswordDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ResetPasswordDialog.class.getSimpleName();

    public Context mContext;

    String retName; // 표시할 닉네임
    public ResetPasswordDialog(Context context, String name) {
        super(context, R.style.DialogFullScreen);

        mContext = context;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_reset_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
