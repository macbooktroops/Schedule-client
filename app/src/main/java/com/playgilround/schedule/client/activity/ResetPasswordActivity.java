package com.playgilround.schedule.client.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.playgilround.common.base.app.BaseActivity;
import com.playgilround.schedule.client.R;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    String retName; // 표시할 닉네임

    EditText etPw;
    EditText etPwConfirm;
    static final String TAG = ResetPasswordActivity.class.getSimpleName();

    @Override
    protected void bindView() {
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        String name = intent.getStringExtra("resultName");
        Log.d(TAG, "name -->" + name);
        TextView title = findViewById(R.id.tvTitle);
        title.setText(name + "님 패스워드를 변경합니다.");

        etPw = findViewById(R.id.editPw);
        etPwConfirm = findViewById(R.id.editPwConfirm);

//        etPwConfirm.setText("ㅇㄹㅁㄴㅇㄹㄷㄷ");

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.tvConfirm:

                String strPw = etPw.getText().toString().trim();
                String strPwConfirm = etPwConfirm.getText().toString().trim();

                if (checkConfirmPassWord(strPw, strPwConfirm)) {
                    Log.d(TAG, "confirm test ->" + strPw + "--"+ strPwConfirm);

                } else {
                    Toast.makeText(this, "패스워드가 다르게 입력되었습니다.", Toast.LENGTH_LONG).show();
                }

        }
    }

    /**
     * 패스워드, 패스워드 확인칸에 동일하게 입력했는지,
     * 추후에는 패스워드 확인 EditText 포커스 사라지면
     * 바로 검사할 수있도록 수정예정
     * @param pw
     * @param pwConfirm
     * @return
     */
    public boolean checkConfirmPassWord(String pw, String pwConfirm) {
        Log.d(TAG, "check pw ->" + pw + "--" + pwConfirm);
        if (pw.equals(pwConfirm)) {
            Log.d(TAG, "samePassword");
            return true;
        } else {
            Log.d(TAG, "different password");
            return false;
        }
    }
}
