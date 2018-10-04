package com.playgilround.schedule.client.Friend;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.schedule.client.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 유저 검색 결과 다이얼로그
 */
public class UserSearchFragment extends DialogFragment {

    static final String TAG = UserSearchFragment.class.getSimpleName();

    static String resName, resBirth;

    static int resId;

    String nickName;
    SharedPreferences pref;

    TextView tvName, tvBirth;
    Button btnOK, btnCancel;

    static boolean resIsFriend;

    public static UserSearchFragment getInstance(int id, String name, String birth, boolean isFriend) {

        resId = id;
        resName = name;
        resBirth = birth;
        resIsFriend = isFriend;

        UserSearchFragment fragment = new UserSearchFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.user_search, container);

        tvName = rootView.findViewById(R.id.searchName);
        tvBirth = rootView.findViewById(R.id.searchBirth);

        tvName.setText(resName);
        tvBirth.setText(resBirth);
        pref = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        nickName = pref.getString("loginName", "");

        btnOK = rootView.findViewById(R.id.btnUserReq);
        btnOK.setOnClickListener(l -> {
            if (nickName.equals(resName)) {
                Log.d(TAG, "same nickname");
                Toast.makeText(getActivity(), "자기 자신은 친구 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "check is Friends? ->" + resIsFriend);
                if (!resIsFriend) {
                    //친구가 안되있는 유저
                    Log.d(TAG, "try new friend...-->" + resId);


                    JsonObject jsonObject = new JsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(resId);
//                    jsonObject.addProperty("user_ids", String.valueOf(jsonArray));

//                    JsonObject userJsonObject = new JsonObject();
//                    userJsonObject.addProperty("", resId);
//                    userJsonObject.addProperty();
//                    JsonArray jsonArray = jsonObject.getAsJsonArray("user_ids");
//                    jsonArray.add(resId);
//                    JsonElement element
                    jsonObject.add("user_ids", jsonArray);

                    String authToken = pref.getString("loginToken", "default");

                    /**
                     * {
                     * "user_ids": [1]
                     * }
                     */
                    Log.d(TAG, "jsonObject - >" + jsonObject);

                    Retrofit retrofit = APIClient.getClient();
                    APIInterface newFriendAPI = retrofit.create(APIInterface.class);
                    Call<JsonObject> result = newFriendAPI.postNewFriend(jsonObject, authToken);

                    result.enqueue(new Callback<JsonObject>() {
                        String error;
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "response new friend -->" + response.body().toString());
                            } else {
                                try {
                                    error = response.errorBody().string();
                                    Log.d(TAG, "response new friend error ->" + error);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d(TAG, "response failure -->" + t.toString());
                        }
                    });

                }
//                JsonObject
            }

        });
        btnCancel = rootView.findViewById(R.id.btnUserCancel);
        btnCancel.setOnClickListener(c -> {
            Log.d(TAG, "cancel btn");
            getDialog().dismiss();
        });



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart User Search");

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
