package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.playgilround.schedule.client.R;

/**
 * 18-10-10
 * push 를 통해 앱 실행후,
 * 친구 수락 받을 건지 물어보는 다이얼로그
 */
public class FriendAssentDialog extends Dialog implements View.OnClickListener{

    static final String TAG = FriendAssentDialog.class.getSimpleName();

    public Context mContext;

    private OnFriendAssentSet mOnFriendAssentSet;

    public FriendAssentDialog(Context context, OnFriendAssentSet onFriendAssentSet) {
        super(context, R.style.DialogFullScreen);
        mContext = context;
        mOnFriendAssentSet = onFriendAssentSet;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_friend_assent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

        public interface OnFriendAssentSet {
            void onFriendAssent();
        }
}
