package com.playgilround.schedule.client.base.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 기본 액티비티 (상속)
 * 18-05-22
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //extends
    protected abstract void bindView();

    /**
     * 동적 데이터 요청
     */
    protected void initData() {

    }

    /**
     * 정적 데이터 바인딩
     */
    protected void bindData() {

    }

    //generic class
    protected <VT extends View> VT searchViewById(int id) {
        VT view = (VT) findViewById(id);
        if (view == null) {
            throw new NullPointerException("This resource id is invalid");

        }

        return view;
    }
}
