package com.playgilround.common.base.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 18-05-24
 * 기본이 되는 Fragment
 *
 */
public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected View mView;


    //Create view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mView = initContentView(inflater, container);

        if (mView == null) {
            throw new NullPointerException("Fragment content view is null...");
        }

        bindView() ;
        return mView;
    }

    //Activity create
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    //하위클래스 강제사용 추상메소드
    @Nullable
    protected abstract View initContentView(LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public void onResume() {
        super.onResume();
        bindData();
    }

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

    protected <VT extends View> VT searchViewById(int id) {
        if (mView == null) {
            throw new NullPointerException("Fragment content view is null...");
        }
        VT view = (VT) mView.findViewById(id);
        if (view == null) {
            throw new NullPointerException("This resource id is invalid");
        }

        return view;
    }
}
