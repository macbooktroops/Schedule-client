package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.playgilround.schedule.client.adapter.RequestShareAdapter;

/**
 * 18-11-02
 * 공유 요청 온 스케줄 다이얼로그
 */
public class RequestShareDialog extends Dialog implements View.OnClickListener {

    static final String TAG = RequestShareDialog.class.getSimpleName();

    public Context mContext;
    RequestShareAdapter reqAdapter;
    public interface OnRequestScheListener {
        void onRequestSche();
    }
}
