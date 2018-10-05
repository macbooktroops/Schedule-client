package com.playgilround.schedule.client.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.fragment.FriendHolder;

/**
 * 18-10-05
 * 현재 자신의 친구 데이터 어댑터
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {

    static final String TAG = FriendAdapter.class.getSimpleName();

    @Override
    public FriendHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend, parent, false);

        FriendHolder holder = new FriendHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");

        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.userNickName.setText("테스트");
        holder.userBirth.setText("1997-08-02");
        holder.userConnect.setText("접속 중");
    }

    @Override
    public int getItemCount() {
        return 10;
    };
}
