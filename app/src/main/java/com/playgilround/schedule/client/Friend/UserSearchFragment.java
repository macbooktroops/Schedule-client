package com.playgilround.schedule.client.Friend;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 유저 검색 결과 다이얼로그
 */
public class UserSearchFragment extends DialogFragment {

    static final String TAG = UserSearchFragment.class.getSimpleName();

    static String resName, resBirth;

    TextView tvName, tvBirth;
    public static UserSearchFragment getInstance(String name, String birth) {

        resName = name;
        resBirth = birth;
        UserSearchFragment fragment = new UserSearchFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.user_search, container);

        tvName = rootView.findViewById(R.id.searchName);
        tvBirth = rootView.findViewById(R.id.searchBirth);

        tvName.setText(resName);
        tvBirth.setText(resBirth);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart User Search");

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
