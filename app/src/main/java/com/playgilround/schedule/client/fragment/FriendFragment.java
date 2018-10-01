package com.playgilround.schedule.client.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playgilround.common.base.app.BaseFragment;
import com.playgilround.schedule.client.R;

/**
 * 18-10-01
 * 친구 관련 Fragment
 */
public class FriendFragment extends BaseFragment {

    static final String TAG = FriendFragment.class.getSimpleName();
    SharedPreferences pref;

    private TextView mainText;
    public static FriendFragment getInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    protected void bindView() {
        pref = getContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);

        String nickName = pref.getString("loginName", "");
        Log.d(TAG, "friend nickName -->" + nickName);

        mainText = searchViewById(R.id.mainNickName);
        mainText.setText(nickName);


    }
}
