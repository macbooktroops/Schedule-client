package com.playgilround.schedule.client.Friend;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.playgilround.schedule.client.R;

/**
 * 유저 검색 결과 다이얼로그
 */
public class UserSearchFragment extends DialogFragment {

    static final String TAG = UserSearchFragment.class.getSimpleName();

    static String resName, resBirth;

    String nickName;
    SharedPreferences pref;

    TextView tvName, tvBirth;
    Button btnOK, btnCancel;
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
        pref = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        nickName = pref.getString("loginName", "");

        btnOK = rootView.findViewById(R.id.btnUserReq);
        btnOK.setOnClickListener(l -> {
            if (nickName.equals(resName)) {
                Log.d(TAG, "same nickname");
                Toast.makeText(getActivity(), "자기 자신은 친구 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
            } else {
//                JsonObject
            }

        });
        btnCancel = rootView.findViewById(R.id.btnUserCancel);
        btnCancel.setOnClickListener(c -> {
            Log.d(TAG, "cancel btn");
            getDialog().dismiss();
        });



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart User Search");

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
