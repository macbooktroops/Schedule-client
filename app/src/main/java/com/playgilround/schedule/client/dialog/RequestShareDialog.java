package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.adapter.RequestShareAdapter;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * 18-11-02
 * 공유 요청 온 스케줄 다이얼로그
 */
public class RequestShareDialog extends Dialog implements View.OnClickListener {

    static final String TAG = RequestShareDialog.class.getSimpleName();

    public Context mContext;
    RequestShareAdapter reqAdapter;

    RecyclerView requestShareRecycler;
    private OnRequestScheListener mOnRequestScheListener;

    ArrayList retArrId, retArrTitle, retArrTime;
    RealmList retArrName;

    private TextView tvTitle;
    public RequestShareDialog(Context context, OnRequestScheListener onRequestScheListener,
                              ArrayList arrId, RealmList arrName, ArrayList arrTitle, ArrayList arrTime) {
        super(context, R.style.DialogFullScreen);

        mOnRequestScheListener = onRequestScheListener;
        mContext = context;

        retArrId = arrId;
        retArrName = arrName;
        retArrTitle = arrTitle;
        retArrTime = arrTime;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_request_schedule);
        requestShareRecycler = findViewById(R.id.reqScheduleRecycler);
        requestShareRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        reqAdapter = new RequestShareAdapter(getContext(), retArrId, retArrName, retArrTitle, retArrTime);
        requestShareRecycler.setAdapter(reqAdapter);

        reqAdapter.setItemClick(new RequestShareAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int id, String time) {
                Log.d(TAG, "Success Click ->" + id + "/" + time);

                dismiss();
            }
        });

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setText("스케줄 요청 목록");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;

            case R.id.tvConfirm:
                if (mOnRequestScheListener != null) {
                    mOnRequestScheListener.onRequestSche();
                }
                dismiss();
                break;
        }

    }
    public interface OnRequestScheListener {
        void onRequestSche();
    }
}
