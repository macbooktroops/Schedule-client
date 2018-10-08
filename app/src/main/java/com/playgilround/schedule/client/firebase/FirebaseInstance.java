package com.playgilround.schedule.client.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * 18-09-30
 * Firebase Instance Id Service
 */
public class FirebaseInstance extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseInstance.class.getSimpleName();

    //token recreate
    @Override
    public void onTokenRefresh() {
        //Get updated InstanceId Token..
        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "token --->" + token);
    }


}
