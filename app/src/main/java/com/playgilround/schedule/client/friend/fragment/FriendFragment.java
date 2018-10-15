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
import com.playgilround.schedule.client.dialog.RequestFriendDialog;
import com.playgilround.schedule.client.friend.adapter.RequestFriendAdapter;
import com.playgilround.schedule.client.friend.json.UserJsonData;
import com.playgilround.schedule.client.friend.adapter.FriendAdapter;
import com.playgilround.schedule.client.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class FriendFragment extends BaseFragment implements MaterialSearchBar.OnSearchActionListener, RequestFriendDialog.OnRequestFriendSet {

    private List<String> lastSearches;
    private MaterialSearchBar searchBar;
    String authToken;

    static final String TAG = FriendFragment.class.getSimpleName();
    SharedPreferences pref;

    private TextView mainText;
    private ImageButton refreshBtn;

    private Button reqFrientBtn; //친구 요청 온 사람만 체크

    private boolean isInit = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String nickName;

//    private DialogFragment mUserSearchFragment;

    RecyclerView friendRecycler;
    FriendAdapter adapter;

    static String resPush;

    //친구데이터 저장 ArrayList
    ArrayList<String> arrName;
    ArrayList<String> arrBirth;

    //친구 요청중이고, 친구 요청받은 ArrayList
    ArrayList<String> arrReqName;
    ArrayList<Integer> arrReqId;

    String name;
    String formattedDate;

    RequestFriendDialog mRequestFriendDialog;

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

        refreshFriend(new ApiCallback() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFail(String result) {

            }
        });

//        String authToken = pref.getString("loginToken", "default");

        //자신과 친구인 사람 데이터 얻기
        refreshBtn = searchViewById(R.id.btnRefreshF);
        refreshBtn.setOnClickListener(l -> {
//            refreshFriend();
            chkNewFriend();
        });

        //자신에게온 친구요청만 보기
        reqFrientBtn = searchViewById(R.id.chkFriend);
        reqFrientBtn.setOnClickListener(l -> {
            chkRequestFriend();
        });
        //restore last queries from disk
//        lastSearches = load
    }

    /**
     * 자신과 친구인 사람 리스트 얻기,
     * 자신에게 요청온 친구 요청중인 리스트 얻기.
     */
    public void refreshFriend(final ApiCallback callback) {
        Log.d(TAG,"refresh Friend");
        Retrofit retrofit = APIClient.getClient();
        APIInterface getFriendAPI = retrofit.create(APIInterface.class);
        Call<JsonArray> result = getFriendAPI.getFriendSearch(authToken);

        result.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "response search friend -->" + response.body().toString());

                    arrName = new ArrayList<>(); //친구이름 목록 ArrayList
                    arrBirth = new ArrayList<>(); //친구생년월일 목록 ArrayList
                    arrReqName = new ArrayList<>();
                    arrReqId = new ArrayList<>();
                    String strSearch = response.body().toString();

                    Type list = new TypeToken<List<UserJsonData>>() {
                    }.getType();


                    List<UserJsonData> userData = new Gson().fromJson(strSearch, list);

                    Log.d(TAG, "userData size -->" + userData.size());

                    for (int i = 0; i < userData.size(); i++) {
                        int id = userData.get(i).id;
                        name = userData.get(i).name;
                        String email = userData.get(i).email;
                        long birth = userData.get(i).birth;
                        int request = userData.get(i).request;
                        //0 -> 자신한테 온 요청 1-> 자기가 건 요청

                        int assent = userData.get(i).assent;
                        Date date = new Date(birth * 1000L);
                        // GMT(그리니치 표준시 +9 시가 한국의 표준시
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                        formattedDate = sdf.format(date);


                        Log.d(TAG, "response search data -->" + id + "--" + name + "--" + email + "--" + formattedDate + "--" +request + "--" + assent);

                        if (assent == 2) {
                            arrName.add(name);
                            arrBirth.add(formattedDate);
//                            arrFriend.add(new ArrayFriend(arrName.get(i), arrBirth.get(i)));
                        } else if (assent == 1 && request == 0) {
                            arrReqId.add(id);
                            arrReqName.add(name);
//                            arrReqBirth.add(formattedDate);
                            //아직 친구 요청중이고, 내가요청을 받은 상태.
//                            Log.d(TAG, "arrRequest -->" + arrReqFriend.size());
//                            arrReqFriend.add(new ArrayRequestFriend(arrName.get(i), arrBirth.get(i)));

                        }
                        //아직 친구 요청중이고, 내가 친구 요청한 상태는 따로 add하지않음.
                    }


                    adapter = new FriendAdapter(getActivity(), arrName, arrBirth);
                    friendRecycler.setAdapter(adapter);

                    callback.onSuccess("success");

                } else {
                    try {
//                        Log.d(TAG, "response search friend error ->" + response.errorBody().string());
                            String error = response.errorBody().string();

                            Log.d(TAG, "response Detail Error -->" + error);

                            Result result = new Gson().fromJson(error, Result.class);

//                            int code = result.code;
                            List<String> message = result.message;

                            Log.d(TAG, "Friend Search Info fail...-->"  + message);


                            if (message.contains("Unauthorized auth_token.")) {
                                Log.d(TAG, "message ->" + message);
                                Toast.makeText(getContext(), "새로고침에 실패했습니다. 재로그인해주세요.", Toast.LENGTH_LONG).show();
                            }
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

    }
    //자신과 친구인 데이터 얻기
    public void chkNewFriend() {
        refreshFriend(new ApiCallback() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFail(String result) {

            }
        });
    }

    //자신에게 온 친구 요청중인 데이터
    public void chkRequestFriend() {
        refreshFriend(new ApiCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "check RequestFriend -->" +arrReqName.size());

                if (mRequestFriendDialog == null) {
                    mRequestFriendDialog = new RequestFriendDialog(getContext(), FriendFragment.this, nickName, arrReqName, arrReqId);
                    mRequestFriendDialog.show();
                }

                mRequestFriendDialog = null;
            }

            @Override
            public void onFail(String result) {

            }
        });

    }

    //친구 요청중인 사람 클릭 후 확인 누르면.
    @Override
    public void onRequestSet() {
        Log.d(TAG, "onRequestSet -----");
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
                        int userFriend = userList.isFriend;


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

                            /**
                             * userFriend 0 = 친구 아닌 상태
                             * userFriend 1 = 친구 요청 중
                             * userFriend 2 = 친구
                             */
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

    public interface ApiCallback{
        void onSuccess(String result);
        void onFail(String result);
    }
}
