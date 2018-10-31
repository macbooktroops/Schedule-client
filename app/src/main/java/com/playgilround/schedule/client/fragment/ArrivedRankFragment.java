package com.playgilround.schedule.client.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.adapter.RankingAdapter;

import java.util.ArrayList;

/**
 * 18-10-31
 * 도착 랭킹 화면 표시 프래그먼트
 * 도착 완료를 눌렀을 때 표시 됩니다.
 */
public class ArrivedRankFragment extends DialogFragment implements View.OnClickListener {

    static final String TAG = ArrivedRankFragment.class.getSimpleName();

    static ArrayList retName = new ArrayList();
    static ArrayList retArrived = new ArrayList();

    RecyclerView rvRank;

    RankingAdapter adapter;
    public static ArrivedRankFragment getInstance(ArrayList arrName, ArrayList arrArrived) {
        retName = arrName;
        retArrived = arrArrived;

        ArrivedRankFragment fragment = new ArrivedRankFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_friend, container); //fragment_schedule_friend.xml 과 동일.

        rvRank = rootView.findViewById(R.id.rvFriend);
        rvRank.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new RankingAdapter(getActivity(), retName);
        rvRank.setAdapter(adapter);

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
                dismiss();
//                dismiss();
        }
    }
}
