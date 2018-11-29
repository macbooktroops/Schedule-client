package com.playgilround.schedule.client.facebook;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.kakao.usermgmt.request.MeRequest;

import org.json.JSONObject;
//http://re-build.tistory.com/11
public class LoginCallback implements FacebookCallback<LoginResult> {

    static final String TAG = LoginCallback.class.getSimpleName();
    //로그인 성공 시 호출 Access Token 발급 성공
    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "onSuccess");
        requestMe(loginResult.getAccessToken());
    }

    //로그인 창 닫을 경우 호출
    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel");
    }

    //로그인 실패 시 호출
    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "onError : error" + error.getMessage());
    }

    //사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "onComplete ->" + object.toString());
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}

