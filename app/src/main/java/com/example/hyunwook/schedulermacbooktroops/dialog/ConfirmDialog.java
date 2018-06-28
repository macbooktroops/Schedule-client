package com.example.hyunwook.schedulermacbooktroops.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-06-27
 * 완료된 스케줄 삭제 다이얼로
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener{

    private TextView tvTitle;
    private String mTitle;
    private boolean mAutoDismiss;
    private OnClickListener mOnClickListener;

    public ConfirmDialog(Context context, int id, OnClickListener onClickListener) {
        this(context, context.getString(id), onClickListener);
    }

    public ConfirmDialog(Context context, String title, OnClickListener onClickListener) {
        this(context, title, onClickListener, true);
    }

    public ConfirmDialog(Context context, String title, OnClickListener onClickListener, boolean autoDismiss) {
        super(context, R.style.DialogFullScreen);

        mTitle = title;
        mOnClickListener = onClickListener;
        mAutoDismiss = autoDismiss;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_confirm);
    }

        public interface OnClickListener {
            void onCancel();  //cancel

            void onConfirm();; //ok
        }

        @Override
        public void dismiss() {

        }
}
