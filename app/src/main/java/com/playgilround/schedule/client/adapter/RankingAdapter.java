package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.holder.RankHolder;

import java.util.ArrayList;

/**
 * 18-10-31
 * 도착 랭킹 어댑터
 */
public class RankingAdapter extends RecyclerView.Adapter<RankHolder> {

    static final String TAG = RankingAdapter.class.getSimpleName();
    Context context;

    ArrayList retName;

    private ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view, int position, String name);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public RankingAdapter(Context context, ArrayList name) {
        this.context = context;

        retName = name;
    }

    @Override
    public RankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rank, parent, false);

        RankHolder holder = new RankHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RankHolder holder, int position) {
        holder.rankImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.rankCount.setText(String.valueOf(position +1) + "등");
        holder.rankName.setText(retName.get(position).toString());

        holder.rankRelative.setOnClickListener(l -> {
            Log.d(TAG, "this view name rank ->" + holder.rankName.getText());
            String resName = holder.rankName.getText().toString();
            if (itemClick != null) {
                itemClick.onClick(l, position, resName);
            }
        });


    }

    @Override
    public int getItemCount() {
        return retName.size();
    }


}
