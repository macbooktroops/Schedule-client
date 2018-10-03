package com.playgilround.schedule.client.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.common.base.app.BaseFragment;
import com.playgilround.schedule.client.R;

import java.util.List;

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


    static final String TAG = FriendFragment.class.getSimpleName();
    SharedPreferences pref;

    private TextView mainText;

    private boolean isInit = true;
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

        String nickName = pref.getString("loginName", "");
        Log.d(TAG, "friend nickName -->" + nickName);

        mainText = searchViewById(R.id.mainNickName);
        mainText.setText(nickName);

        searchBar = searchViewById(R.id.searchBar);
        searchBar.setHint("유저 검색");
        searchBar.setSpeechMode(true);

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
        Toast.makeText(getContext(), "Search " + s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSearchConfirmed(CharSequence text) {

        if (isInit) {
            Log.d(TAG, "Confirmed --->" + text.toString());

            JsonObject jsonObject = new JsonObject();
            JsonObject userJsonObject = new JsonObject();

            userJsonObject.addProperty("name", text.toString());

            jsonObject.add("user", userJsonObject);
            String authToken = pref.getString("loginToken", "default");

            Log.d(TAG, "authToken ->" + authToken);

            Log.d(TAG, "friend body ->" + jsonObject);

            Retrofit retrofit = APIClient.getClient();
            APIInterface userAPI = retrofit.create(APIInterface.class);
            Call<JsonObject> result = userAPI.postUserSearch(jsonObject, authToken);

            result.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "response FCM -> " + response.body().toString());
                    } else {
                        try {
                            Log.d(TAG, "response error FCM - >" + response.errorBody().string());
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
