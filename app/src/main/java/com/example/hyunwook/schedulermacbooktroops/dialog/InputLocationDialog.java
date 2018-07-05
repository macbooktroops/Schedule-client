package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-07-05
 * 위치 설정 다이얼로그
 */
public class InputLocationDialog extends Dialog implements View.OnClickListener{

    private OnLocationBackListener mOnLocationBackListener;
    private EditText etLocationContent;

    public InputLocationDialog(Context context, OnLocationBackListener onLocationBackListener) {
        super(context, R.style.DialogFullScreen);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_input_location);
        etLocationContent = (EditText) findViewById(R.id.etLocationContent);
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
                if (mOnLocationBackListener != null) {
                    mOnLocationBackListener.onLocationBack(etLocationContent.getText().toString());
                }

                dismiss();
                break;
        }
    }
        public interface OnLocationBackListener {
            void onLocationBack(String text);
        }
}
