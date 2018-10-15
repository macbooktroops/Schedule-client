package com.playgilround.schedule.client.friend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.friend.FriendHolder;

import java.util.ArrayList;

/**
 * 18-10-15
 * 자신에게 요청이 오고,
 * 친구 요청중인 상태의 데이터 어댑터
 */
public class RequestFriendAdapter extends RecyclerView.Adapter<FriendHolder> {

    Context context;
    ArrayList retName;
    public RequestFriendAdapter(Context context, ArrayList name) {
        this.context = context;

        retName = name;
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_request_friend, parent, false);

        FriendHolder holder = new FriendHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.userNickName.setText(retName.get(position).toString());
//        holder.userBirth.setText(retBirth.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return retName.size();
    }
}
