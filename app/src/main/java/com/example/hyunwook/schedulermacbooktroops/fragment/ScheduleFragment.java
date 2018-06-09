package com.example.hyunwook.schedulermacbooktroops.fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.base.app.BaseFragment;
import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-05-25
 * 스케줄 메인 하ㅗ면
 */
public class ScheduleFragment extends BaseFragment {

    //create fragment init
    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
}
