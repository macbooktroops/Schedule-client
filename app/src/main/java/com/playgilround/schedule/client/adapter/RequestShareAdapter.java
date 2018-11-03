package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.holder.ShareHolder;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * 18-11-02
 * 스케줄 공유 요청중인 스케줄 어댑터
 */
public class RequestShareAdapter extends RecyclerView.Adapter<ShareHolder> {

    static final String TAG = RequestShareAdapter.class.getSimpleName();

    Context context;
    RealmList retName;
    ArrayList retId, retTitle, retTime;

    public RequestShareAdapter(Context context, ArrayList id, RealmList name, ArrayList title, ArrayList time) {
        this.context = context;
        retId = id;
        retName = name;
        retTitle = title;
        retTime = time;
    }

    @Override
    public ShareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_request_schedule, parent, false);

        ShareHolder holder = new ShareHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ShareHolder holder, int position) {
        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.tvName.setText(retName.get(position).toString());
        holder.tvTitle.setText(retTitle.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return retName.size();
    }
}
