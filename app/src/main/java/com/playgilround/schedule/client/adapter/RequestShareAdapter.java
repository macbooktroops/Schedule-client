package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private ItemClick itemClick;

    public RequestShareAdapter(Context context, ArrayList id, RealmList name, ArrayList title, ArrayList time) {
        this.context = context;
        retId = id;
        retName = name;
        retTitle = title;
        retTime = time;
    }

    //클릭 인터페이스
    public interface ItemClick {
        public void onClick(View view, int position, int id, String time, String name, String title);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
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

        holder.tvId.setText(retId.get(position).toString());
        holder.userView.setOnClickListener(l -> {
            Log.d(TAG, "this view ->" + holder.tvId.getText() + "--" + retTime.get(position).toString());

            int id = Integer.valueOf(holder.tvId.getText().toString());
            String time = retTime.get(position).toString();
            String name = holder.tvName.getText().toString();
            String title = holder.tvTitle.getText().toString();

            if (itemClick != null) {
                itemClick.onClick(l, position, id, time, name, title);
            }
//            String
        });
    }

    @Override
    public int getItemCount() {
        return retName.size();
    }
}
