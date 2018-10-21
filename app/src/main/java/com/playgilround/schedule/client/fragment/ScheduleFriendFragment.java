package com.playgilround.schedule.client.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.LoginActivity;
import com.playgilround.schedule.client.adapter.ChoiceFriendAdapter;
import com.playgilround.schedule.client.utils.ScheduleFriendItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 18-10-20
 * 스케줄 저장 전,
 * 스케줄을 공유 할 친구 선택 다이얼로그
 */
public class ScheduleFriendFragment extends DialogFragment implements View.OnClickListener {

    static final String TAG = ScheduleFriendFragment.class.getSimpleName();

    static ArrayList retArr = new ArrayList();
    RecyclerView rvSchedule;

    ChoiceFriendAdapter adapter;

    TextView tvConfirm;
    static FriendFragment.ApiCallback retCallback;

    public static ScheduleFriendFragment getInstance(ArrayList arrName, FriendFragment.ApiCallback callback) {
        retArr = arrName;

        retCallback = callback;
        Log.d(TAG, "retArr -->" + retArr);

        ScheduleFriendFragment fragment = new ScheduleFriendFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_friend, container);

        rvSchedule = rootView.findViewById(R.id.rvFriend);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this.getActivity()));

//        retArr = getList();
        List<ScheduleFriendItem> list = getList();
        adapter = new ChoiceFriendAdapter(getActivity(), list);
        rvSchedule.setAdapter(adapter);

        rootView.findViewById(R.id.tvCancel).setOnClickListener(this);
        rootView.findViewById(R.id.tvConfirm).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                selectedClick();
//                dismiss();
        }
    }

    public void selectedClick() {
        List list = adapter.getSelectedItem(); //체크 된 리스트
        if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < list.size(); index++) {
                ScheduleFriendItem item = (ScheduleFriendItem) list.get(index);
                sb.append(item.getName()).append("\n");
            }
            Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "retCallback state ---..");
            retCallback.onSuccess("success");
            dismiss();
        } else {
            Toast.makeText(getActivity(), "공유할 친구를 선택해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 친구 목록 ArrayList 를
     * ScheduleFriendItem 에 저장.
     */
    private List<ScheduleFriendItem> getList() {
        List<ScheduleFriendItem> list = new ArrayList<>();

        for (int i = 0; i < retArr.size(); i++) {
            ScheduleFriendItem item = new ScheduleFriendItem();
            item.setName(retArr.get(i).toString());
            list.add(item);
        }

        return list;
    }
}


