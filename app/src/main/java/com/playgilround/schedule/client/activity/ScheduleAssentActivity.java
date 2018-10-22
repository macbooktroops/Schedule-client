package com.playgilround.schedule.client.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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
 * 18-10-21
 * 스케줄 푸쉬 받을 때
 * 스케줄 공유 수락, 거부 요청 다이얼로그
 */
public class ScheduleAssentActivity extends Activity implements View.OnClickListener {

    static final String TAG = ScheduleAssentActivity.class.getSimpleName();

    public Context mContext;


    TextView tvAssent, tvResult;
    String retName, retTitle;

    int retId;
    Intent intent;

    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_schedule_assent);

        intent = getIntent();
        retName = intent.getStringExtra("PushName");
        retId = intent.getIntExtra("PushId", 0);
        retTitle = intent.getStringExtra("PushTitle");

        tvAssent = findViewById(R.id.tvAssent);
        tvAssent.setText(retName +" 님에 " + "'"+ retTitle +"'");

        tvResult = findViewById(R.id.resultText);
        tvResult.setText("이 스케줄을 앞으로 \n 공유하시겠어요?");
        tvResult.setGravity(Gravity.CENTER);

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);

        Log.d(TAG, "ScheduleAssentActivity ->" + retName + "--" + retId + "--" + retTitle);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvNegative:
                onScheAssent(false);

                finish();
                break;
            case R.id.tvPositive:
                onScheAssent(true);

                finish();
        }
    }

    //스케줄 수락, 거부 푸쉬 처리

    public void onScheAssent(boolean state) {

        pref = getSharedPreferences("loginData", Activity.MODE_PRIVATE);

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
                            Toast.makeText(getApplicationContext(), "Auth Token error.", Toast.LENGTH_LONG).show();
                        } else if (message.contains("Not found schedule.")) {
                            Toast.makeText(getApplicationContext(), "스케줄이 없습니다.", Toast.LENGTH_LONG).show();
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
}
