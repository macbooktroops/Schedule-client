package com.playgilround.schedule.client.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 18-10-05
 * 친구 관련 ViewHolder
 */
public class FriendHolder extends RecyclerView.ViewHolder {

    public ImageView userImage; //유저 이미지
    public TextView userNickName; //유저 닉네임
    public TextView userBirth; //유저 생년월일
    public TextView userConnect; //유저 접속 중 여
    public View userView; //해당 뷰 전체
    public TextView userId;
    public FriendHolder(View itemView) {
        super(itemView);
        userImage = itemView.findViewById(R.id.userImage);
        userNickName = itemView.findViewById(R.id.userNickName);
        userBirth = itemView.findViewById(R.id.userBirth);
        userView = itemView.findViewById(R.id.friendCard);
        userId = itemView.findViewById(R.id.userId);

    }
}
