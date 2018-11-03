package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.adapter.RequestShareAdapter;

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

    public RequestShareDialog(Context context, OnRequestScheListener onRequestScheListener, String name, String title) {
        super(context, R.style.DialogFullScreen);

    }

    @Override
    public void onClick(View v) {

    }
    public interface OnRequestScheListener {
        void onRequestSche();
    }
}
