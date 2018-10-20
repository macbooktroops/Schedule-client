package com.playgilround.schedule.client.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;

import java.util.ArrayList;

/**
 * 18-10-20
 * 스케줄 저장 전,
 * 스케줄을 공유 할 친구 선택 다이얼로그
 */
public class ScheduleFriendFragment extends DialogFragment {

    static final String TAG = ScheduleFriendFragment.class.getSimpleName();

    static ArrayList<String> retArr = new ArrayList<>();
    RecyclerView rvSchedule;


    public static ScheduleFriendFragment getInstance(ArrayList arrName) {
        retArr = arrName;

        ScheduleFriendFragment fragment = new ScheduleFriendFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_friend, container);

        rvSchedule = rootView.findViewById(R.id.rvFriend);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this.getActivity()));

    }
}
