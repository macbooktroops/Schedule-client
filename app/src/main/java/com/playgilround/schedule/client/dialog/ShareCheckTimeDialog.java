package com.playgilround.schedule.client.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.gson.Result;
import com.playgilround.schedule.client.retrofit.APIClient;
import com.playgilround.schedule.client.retrofit.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-11-03
 * RequestShareDialog 에서 항목 클릭 시,
 * 수락 할건지, 거부 할 건지 스케줄 요청 시간과 표시 되는 다이얼로그.
 */
public class ShareCheckTimeDialog extends Dialog implements View.OnClickListener {

    static final String TAG = ShareCheckTimeDialog.class.getSimpleName();

    public Context mContext;
    private OnRequestScheListener mOnRequestScheListener;
    SharedPreferences pref;

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

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNegative:
                onScheAssent(false);
                dismiss();
                break;
            case R.id.tvPositive:
                onScheAssent(true);
                dismiss();
                break;
        }
    }
//      if (mOnRequestScheListener != null) {
//        mOnRequestScheListener.onRequestSche();
//    }
    //스케줄 수락, 거부 푸쉬 처리

    public void onScheAssent(boolean state) {

        pref = getContext().getSharedPreferences("loginData", Activity.MODE_PRIVATE);

        String authToken = pref.getString("loginToken", "default");

        Log.d(TAG, "onScheAssent ---" + state + authToken + retId);
        /**
         * {
         *       "answer": true
         *  }
         */
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("answer", state);

        Log.d(TAG, "onSche json -->" + jsonObject);
        Retrofit retrofit = APIClient.getClient();
        APIInterface assentScheAPI = retrofit.create(APIInterface.class);
        Call<JsonObject> result = assentScheAPI.postScheduleAssent(jsonObject, authToken, retId);

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //수락처리, 거절 처리
                if (response.isSuccessful()) {
                    Log.d(TAG, "response schedule assent ----" + response.body().toString());
                } else {
                    try {
                        String error = response.errorBody().string();
                        Log.d(TAG, "response schedule assent fail..." + error);


                        Result result = new Gson().fromJson(error, Result.class);

                        List<String> message = result.message;

                        if (message.contains("Unauthorized auth_token.")) {
                            Toast.makeText(getContext(), "Auth Token error.", Toast.LENGTH_LONG).show();
                        } else if (message.contains("Not found schedule.")) {
                            Toast.makeText(getContext(), "스케줄이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
    public interface OnRequestScheListener {
        void onRequestSche();
    }

}
