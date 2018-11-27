package com.playgilround.schedule.client.kakao;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.playgilround.schedule.client.realm.RealmInit;

/**
 * 18-11-27
 * 카카오톡 로그인을 위해 어댑터 추가.
 */
public class KakaoSDKAdapter extends KakaoAdapter {

    /**
     * Session Config 에 대해서는 default 값이 존재함.
     * 필요한 상황에서만 override 하면됨
     * @return Session설정값
     */

    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[] {AuthType.KAKAO_ACCOUNT};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return false;
            }
        };
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {
            @Override
            public Activity getTopActivity() {
                return RealmInit.getCurrentActivity();
            }

            @Override
            public Context getApplicationContext() {
                return RealmInit.getGlobalApplicationContext();
            }
        };
    }
}
