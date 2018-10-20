package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.holder.ChoiceHolder;
import com.playgilround.schedule.client.holder.FriendHolder;

import java.util.ArrayList;

/**
 * 18-10-20
 * 스케줄 저장 전 공유 친구 선택 어댑터
 */
public class ChoiceFriendAdapter extends RecyclerView.Adapter<ChoiceHolder> {

    Context context;
    ArrayList retName;

    CardView friendCard;


    public ChoiceFriendAdapter(Context context, ArrayList name) {
        this.context = context;
        retName = name;
    }

    @Override
    public ChoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_choice_friend, parent, false);

        ChoiceHolder holder = new ChoiceHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChoiceHolder holder, int position) {
        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.userNickName.setText(retName.get(position).toString());

        holder.friendCard.setOnClickListener(l -> {
            Toast.makeText(context, retName.get(position).toString() + "클릭", Toast.LENGTH_LONG).show();
        });

        holder.friendCard.setSelected(holder.friendCard.isSelected() ? true : false);

    }

    @Override
    public int getItemCount() {
        return retName.size();
    }

}
