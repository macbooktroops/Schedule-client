package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-09-29
 * 아이디, 비밀번호 찾기 다이얼로그
 */
public class SelectFindDialog extends Dialog implements View.OnClickListener {

    static final String TAG = SelectFindDialog.class.getSimpleName();

    public Context mContext;

    private OnFindSetListener mOnFindSetListener;

    private Button  findEmail, findPw;
    private TextView cancel, confirm, check;

    private boolean isChecked = false;
    public SelectFindDialog(Context context, OnFindSetListener onFindSetListener) {
        super(context, R.style.DialogFullScreen);
        mContext = context;
        mOnFindSetListener = onFindSetListener;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_select_account);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        findViewById(R.id.findEmail).setOnClickListener(this);
        findViewById(R.id.findPw).setOnClickListener(this);

        findEmail = findViewById(R.id.findEmail);
        findPw = findViewById(R.id.findPw);

        check = findViewById(R.id.checkFind);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findEmail:
                check.setText("이메일을 찾으시겠어요?");
                isChecked = true;
                break;

            case R.id.findPw:
                check.setText("비밀번호를 찾으시겠어요?");
                isChecked = true;
                break;

            case R.id.tvConfirm:
                if (isChecked) {
                    if (mOnFindSetListener != null) {
                        mOnFindSetListener.onFindSet();
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "찾으실 항목을 클릭해주세요.", Toast.LENGTH_LONG).show();
                }

        }
    }


        public interface OnFindSetListener {
            void onFindSet();
        }
}
