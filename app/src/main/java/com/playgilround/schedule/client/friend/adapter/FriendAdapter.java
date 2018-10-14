package com.playgilround.schedule.client.friend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.friend.FriendHolder;
import com.playgilround.schedule.client.R;

import java.util.ArrayList;

/**
 * 18-10-05
 * 현재 자신의 친구 데이터 어댑터
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {

    static final String TAG = FriendAdapter.class.getSimpleName();

    Context context;
    ArrayList retName, retBirth;
    public FriendAdapter(Context context, ArrayList name, ArrayList birth) {
       this.context = context;
       retName = name;
       retBirth = birth;

    }
    @Override
    public FriendHolder onCreateViewHolder (ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend, parent, false);

        FriendHolder holder = new FriendHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.userNickName.setText(retName.get(position).toString());
        holder.userBirth.setText(retBirth.get(position).toString());
        holder.userConnect.setText("접속 중");
    }

    @Override
    public int getItemCount() {
        return retName.size();
    };
}
