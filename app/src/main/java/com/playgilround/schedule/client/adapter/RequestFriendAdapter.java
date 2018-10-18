package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.holder.FriendHolder;

import java.util.ArrayList;

/**
 * 18-10-15
 * 자신에게 요청이 오고,
 * 친구 요청중인 상태의 데이터 어댑터
 */
public class RequestFriendAdapter extends RecyclerView.Adapter<FriendHolder> {

    static final String TAG = RequestFriendAdapter.class.getSimpleName();
    Context context;
    ArrayList retName, retId;

    private ItemClick itemClick;
    public RequestFriendAdapter(Context context, ArrayList name, ArrayList id) {
        this.context = context;

        retName = name;
        retId = id;
    }

    //각 항목 클릭 인터페이스
    public interface ItemClick {
        public void onClick(View view, int position, String name, int id);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
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

        holder.userId.setText(retId.get(position).toString());
        holder.userView.setOnClickListener(l -> {
            Log.d(TAG, "this view name...-->" + holder.userNickName.getText() + "--" + holder.userId.getText());
            String resName = holder.userNickName.getText().toString();
            int resId = Integer.parseInt(holder.userId.getText().toString());
            if (itemClick != null) {
                itemClick.onClick(l, position, resName, resId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return retName.size();
    }
}
