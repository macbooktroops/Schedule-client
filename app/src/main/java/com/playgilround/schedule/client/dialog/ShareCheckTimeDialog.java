package com.playgilround.schedule.client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playgilround.schedule.client.R;

/**
 * 18-11-03
 * RequestShareDialog 에서 항목 클릭 시,
 * 수락 할건지, 거부 할 건지 스케줄 요청 시간과 표시 되는 다이얼로그.
 */
public class ShareCheckTimeDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ShareCheckTimeDialog.class.getSimpleName();

    public Context mContext;
    private OnRequestScheListener mOnRequestScheListener;

    int retId;
    String retTime, retName, retTitle;
    TextView tvTime, tvAssent;
    public ShareCheckTimeDialog(Context context, int id, String time, String name, String title) {
        super(context, R.style.DialogFullScreen);

        mContext = context;

        retId = id;
        retTime = time;
        retName = name;
        retTitle = title;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_request_assent);

        tvTime = findViewById(R.id.tvTime);
        tvTime.setText(retTime);

        tvAssent = findViewById(R.id.tvAssent);
        tvAssent.setText(retName + "님이 요청 하신 \n" + retTitle + "스케줄을 공유하시겠어요?");
        Log.d(TAG, "Result -> " + retId + "--" + retTime + "--" + retTitle + "--" + retName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNegative:
                break;
            case R.id.tvPositive:
                break;
        }
    }
//      if (mOnRequestScheListener != null) {
//        mOnRequestScheListener.onRequestSche();
//    }

    public interface OnRequestScheListener {
        void onRequestSche();
    }

}
