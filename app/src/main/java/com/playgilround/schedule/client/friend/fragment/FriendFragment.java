package com.playgilround.schedule.client.friend.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.calendar.widget.calendar.retrofit.Result;
import com.playgilround.common.base.app.BaseFragment;
import com.playgilround.schedule.client.friend.UserJsonData;
import com.playgilround.schedule.client.friend.adapter.FriendAdapter;
import com.playgilround.schedule.client.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-10-01
 * 친구 관련 Fragment
 */
public class FriendFragment extends BaseFragment implements MaterialSearchBar.OnSearchActionListener {

    private List<String> lastSearches;
    private MaterialSearchBar searchBar;
    String authToken;

    static final String TAG = FriendFragment.class.getSimpleName();
    SharedPreferences pref;

    private TextView mainText;
    private ImageButton refreshBtn;

    private boolean isInit = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String nickName;
//    private DialogFragment mUserSearchFragment;

    RecyclerView friendRecycler;
    FriendAdapter adapter;


    public static FriendFragment getInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    protected void bindView() {
        pref = getContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);

        nickName = pref.getString("loginName", "");
        Log.d(TAG, "friend nickName -->" + nickName);

        authToken = pref.getString("loginToken", "default");
        Log.d(TAG, "friend nickName -->" + nickName + "--" + authToken);
        mainText = searchViewById(R.id.mainNickName);
        mainText.setText(nickName);

        searchBar = searchViewById(R.id.searchBar);
        searchBar.setHint("유저 검색");
        searchBar.setSpeechMode(false);

        //enable SearchBar callbacks
        searchBar.setOnSearchActionListener(this);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
                isInit = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        friendRecycler = searchViewById(R.id.friendRecycler);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new FriendAdapter();
        friendRecycler.setAdapter(adapter);

//        String authToken = pref.getString("loginToken", "default");

        refreshBtn = searchViewById(R.id.btnRefreshF);
        refreshBtn.setOnClickListener(l -> {
            Log.d(TAG,"refresh Friend");
            Retrofit retrofit = APIClient.getClient();
            APIInterface getFriendAPI = retrofit.create(APIInterface.class);
            Call<JsonArray> result = getFriendAPI.getFriendSearch(authToken);

            result.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "response search friend -->" + response.body().toString());
                    } else {
                        try {
                            Log.d(TAG, "response search friend error ->" + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(TAG, "response search friend failure ->" + t.toString());
                }
            });

        });
        //restore last queries from disk
//        lastSearches = load



    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                Log.d(TAG, "Button Navigation MaterialSearchBar");
                break;
                case MaterialSearchBar.BUTTON_SPEECH:
                    Log.d(TAG, "Button Speech MaterialSearchBar");
                    break;
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
//        Toast.makeText(getContext(), "Search " + s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSearchConfirmed(CharSequence text) {

        if (isInit) {
            Log.d(TAG, "Confirmed --->" + text.toString());

            /**
             * {
             *   "user" : {
             *       "email" : "test4@test.com"
             *     }
             * }
             */
            JsonObject jsonObject = new JsonObject();
            JsonObject userJsonObject = new JsonObject();

            userJsonObject.addProperty("name", text.toString());

            jsonObject.add("user", userJsonObject);
//            authToken = pref.getString("loginToken", "default");

            Log.d(TAG, "authToken ->" + authToken);

            Log.d(TAG, "friend body ->" + jsonObject);

            Retrofit retrofit = APIClient.getClient();
            APIInterface userAPI = retrofit.create(APIInterface.class);
            Call<JsonObject> result = userAPI.postUserSearch(jsonObject, authToken);

            result.enqueue(new Callback<JsonObject>() {
                String error;
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        String strResponse = response.body().toString();

                        Type list = new TypeToken<UserJsonData>() {
                        }.getType();
                        Log.d(TAG, "response FCM -> " + strResponse);

                        //{"id":3,"name":"hyun","email":"c004112@gmail.com","birth":870480000,"is_friend":false}
                        UserJsonData userList = new Gson().fromJson(strResponse, list);

                        int userId = userList.id;
                        String userName = userList.name;
                        String userEmail = userList.email;
                        long userBirth = userList.birth;
                        boolean userFriend = userList.isFriend;


//                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

//                        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date(userBirth));

                            Date date = new Date(userBirth * 1000L);
                            // GMT(그리니치 표준시 +9 시가 한국의 표준시
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                            String formattedDate = sdf.format(date);


//                        long millisecond = Long.parseLong(userBirth);
                        // or you already have long value of date, use this instead of milliseconds variable.
                        Log.d(TAG, "result set ->" + userId + "--" + userName + "--" + userEmail + "--" + userBirth + "--" + formattedDate + "--"+ userFriend);

                        if (userList != null) {
                            //해당 유저가 존재
                            Log.d(TAG, "userList --->" + userName);


//                            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//
//                            if (mUserSearchFragment != null) {
//                                ft.remove(mUserSearchFragment);
//                            }
//                            mUserSearchFragment = UserSearchFragment.getInstance(userName, formattedDate);


//                            final UserSearchFragment us = new UserSearchFragment(userName, formattedDate);
                            final UserSearchFragment us = UserSearchFragment.getInstance(userId, userName,  formattedDate, userFriend);
                            final android.app.FragmentManager fm = getActivity().getFragmentManager();
                            us.show(fm, "TAG");
//                            mUserSearchFragment.show(fm, "TAG");
                        } else {
                            Log.d(TAG, "user is null");
                        }

                    } else {
                        try {
                            error = response.errorBody().string();
                            Log.d(TAG, "response error FCM - >" + error);



                            Result result = new Gson().fromJson(error, Result.class);

                            int code = result.code;
                            List<String> message = result.message;

                            Log.d(TAG, "Friends fail ----> " + code +"--"+ message);


                            if (message.contains("Not found user.") || message.contains("Unauthorized auth_token.")) {
                                Log.d(TAG, "message ->" + message);
                                Toast.makeText(getContext(), "그런 유저는 없어요 ㅋ", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d(TAG, "fail FCM ->" + t.toString());
                }
            });
            isInit = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
