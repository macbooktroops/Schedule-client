package com.playgilround.schedule.client.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.schedule.client.R;

import org.joda.time.DateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-10-10
 * push 를 통해 앱 실행후,
 * 친구 수락 받을 건지 물어보는 액티비티 다이얼로그
 */

public class FriendAssentActivity extends Activity implements View.OnClickListener{

    static final String TAG = FriendAssentActivity.class.getSimpleName();


    public Context mContext;


    TextView tvFriend, tvTime, tvNegative, tvPositive;
    String retName;
    String retId;

    SharedPreferences pref;
    Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_friend_assent);

        intent = getIntent();
        retName = intent.getStringExtra("PushName");
//    }
//    private void initView() {
//        setContentView(R.layout.dialog_friend_assent);

        tvFriend = findViewById(R.id.tvFriends);

      /*  tvNegative = findViewById(R.id.tvNegative);
        tvPositive = findViewById(R.id.tvPositive);
*/
        Log.d(TAG, "retName -->" + retName);

        tvFriend.setText(retName +"님이 \n친구가 되길 원합니다.");

        findViewById(R.id.tvNegative).setOnClickListener(this);
        findViewById(R.id.tvPositive).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNegative:
//                if (mOnFriendAssentSet != null) {
                Log.d(TAG, "Click Friend negative... -->");
                onFriendAssent(false);
//                }
                finish();
                break;

            case R.id.tvPositive:
//                if (mOnFriendAssentSet != null) {
                Log.d(TAG, "Click Friend positive... -->");

                onFriendAssent(true);
//                }
                finish();
        }
    }

    //FriendAssentDialog interface
    /**
     *  앱이 실행중이 아닐 때
     *  푸쉬 메세지 도착 후
     *  친구 요청 버튼 클릭 하기
     *  true 수락 false 거부
     */
//    @Override
    public void onFriendAssent(boolean state) {
        pref = getSharedPreferences("loginData", Activity.MODE_PRIVATE);

        String authToken = pref.getString("loginToken", "default");

        int resPushId = intent.getIntExtra("PushId" ,1);

        Log.d(TAG, "onFriendAssent -----" + resPushId +"--" + authToken + "--" + state);
        DateTime dateTime = new DateTime();
        String today = dateTime.toString("yyyy-MM-dd HH:mm:ss");

        Log.d(TAG, "today time -->" + today);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("answer", state);
        jsonObject.addProperty("answered_at", today);


        Log.d(TAG, "jsonObject -->" + jsonObject);
//        jsonObject.addProperty("answered_at", );
        Retrofit retrofit = APIClient.getClient();
        APIInterface fAssetAPI = retrofit.create(APIInterface.class);
        Call<JsonObject> result = fAssetAPI.postFriendAssent(jsonObject, resPushId, authToken);

        Log.d(TAG, "result value -->" + fAssetAPI.postFriendAssent(jsonObject, resPushId, authToken).request().url().toString());

        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "response assent success ---" + response.body().toString());
                } else {
                    try {
                        Log.d(TAG, "response assent fail -->" + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "response assent error ->" + t.toString());
            }
        });

    }
}


