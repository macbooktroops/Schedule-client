package com.playgilround.schedule.client.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 18-10-31
 * 랭킹 ViewHolder
 */
public class RankHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rankRelative;
    public ImageView rankImage;
    public TextView rankCount;
    public TextView rankName;

    public RankHolder(View itemView) {
        super(itemView);

        rankRelative = itemView.findViewById(R.id.rlRank);
        rankImage = itemView.findViewById(R.id.userImage);
        rankCount = itemView.findViewById(R.id.userRank);
        rankName = itemView.findViewById(R.id.userNickName);
    }



}
