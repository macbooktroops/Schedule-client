package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    TextView tvFriend, tvTime, tvNegative, tvPositive;
    String retName;
    String retId;

    public FriendAssentDialog(Context context, OnFriendAssentSet onFriendAssentSet, String name) {
        super(context, R.style.DialogFullScreen);
        mContext = context;
        mOnFriendAssentSet = onFriendAssentSet;
        retName = name;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_friend_assent);

        tvFriend = findViewById(R.id.tvFriends);

      /*  tvNegative = findViewById(R.id.tvNegative);
        tvPositive = findViewById(R.id.tvPositive);
*/
        Log.d(TAG, "retName -->" + retName);

        tvFriend.setText(retName +"님이 \n친구가 되길 원합니다.");

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNegative:
                if (mOnFriendAssentSet != null) {
                    Log.d(TAG, "Click Friend negative... -->");
                    mOnFriendAssentSet.onFriendAssent(false);
                }
                dismiss();
                break;

            case R.id.tvPositive:
                if (mOnFriendAssentSet != null) {
                    Log.d(TAG, "Click Friend positive... -->");

                    mOnFriendAssentSet.onFriendAssent(true);
                }
                dismiss();
        }
    }

        public interface OnFriendAssentSet {
            void onFriendAssent(boolean state);
        }
}
