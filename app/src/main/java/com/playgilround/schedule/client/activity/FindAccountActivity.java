package com.playgilround.schedule.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.playgilround.schedule.client.R;

/**
 * 18-10-01
 * Find Account
 */
public class FindAccountActivity extends Activity {

    static final String TAG = FindAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String check = intent.getStringExtra("check");

        Log.d(TAG, "check find ->" + check);

        if (check.equals("Email")) {
            setContentView(R.layout.activity_find_email);
        } else if (check.equals("Password")) {
            setContentView(R.layout.activity_find_password);
        }
    }
}
