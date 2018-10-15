package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 18-10-15
 * 자신에게 친구요청이 오고,
 * 친구 요청중인 리스트 볼 수있는 다이얼로그
 */
public class RequestFriendDialog extends Dialog implements View.OnClickListener {

    static final String TAG = RequestFriendDialog.class.getSimpleName();

    public Context mContext;

    private OnRequestFriendSet mOnRequestFriendSet;
    String retName;

    private TextView tvTitle;
    public RequestFriendDialog(Context context, OnRequestFriendSet onRequestFriendSet, String name) {
        super(context, R.style.DialogFullScreen);
        mOnRequestFriendSet = onRequestFriendSet;

        mContext = context;
        retName = name;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_request_friend);

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        tvTitle =  findViewById(R.id.tvTitle);

        tvTitle.setText(retName + "님과 친구가 되고싶은 유저입니다.");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;

            case R.id.tvConfirm:

                if (mOnRequestFriendSet != null) {
                    mOnRequestFriendSet.onRequestSet();
                }
                dismiss();
                break;
        }
    }

    public interface OnRequestFriendSet {
        void onRequestSet();
    }
}
