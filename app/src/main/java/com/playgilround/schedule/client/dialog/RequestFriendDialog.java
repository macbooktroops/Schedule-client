package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.FriendAssentActivity;
import com.playgilround.schedule.client.friend.adapter.RequestFriendAdapter;

import java.util.ArrayList;

/**
 * 18-10-15
 * 자신에게 친구요청이 오고,
 * 친구 요청중인 리스트 볼 수있는 다이얼로그
 */
public class RequestFriendDialog extends Dialog implements View.OnClickListener {

    static final String TAG = RequestFriendDialog.class.getSimpleName();

    public Context mContext;
    RequestFriendAdapter reqAdapter;

    RecyclerView requestFriendRecycler;
    private OnRequestFriendSet mOnRequestFriendSet;
    String retName;

    ArrayList retArrName, retArrId;

    private TextView tvTitle;
    public RequestFriendDialog(Context context, OnRequestFriendSet onRequestFriendSet, String name, ArrayList arrName, ArrayList arrId) {
        super(context, R.style.DialogFullScreen);
        mOnRequestFriendSet = onRequestFriendSet;

        mContext = context;
        retName = name;

        retArrName = arrName;
        retArrId = arrId;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_request_friend);
        requestFriendRecycler = findViewById(R.id.reqFriendRecycler);
        requestFriendRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        reqAdapter = new RequestFriendAdapter(getContext(), retArrName, retArrId);
        requestFriendRecycler.setAdapter(reqAdapter);

        reqAdapter.setItemClick(new RequestFriendAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, String name, int id) {
                Log.d(TAG, "Success Click..-->" + name + "--" + id);

                dismiss();

                Intent intent = new Intent(getContext(), FriendAssentActivity.class);
                intent.putExtra("PushName", name);
                intent.putExtra("PushId", id);
                getContext().startActivity(intent);
            }
        });


        Log.d(TAG, "initView -->"  + retArrName.size());
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        tvTitle =  findViewById(R.id.tvTitle);

        tvTitle.setText(retName + "님에게 온 친구 요청");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;

            case R.id.tvConfirm:
                if (mOnRequestFriendSet != null) {
                    mOnRequestFriendSet.onRequestSet();
                }
                dismiss();
                break;
        }
    }

    public interface OnRequestFriendSet {
        void onRequestSet();
    }
}
