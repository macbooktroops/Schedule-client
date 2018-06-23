package com.example.hyunwook.schedulermacbooktroops.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.base.app.BaseFragment;
import com.example.common.bean.EventSet;
import com.example.hyunwook.schedulermacbooktroops.R;

/**
 * 18-06-19
 * 스케줄 목록을 보고자 할때 표시되는 프레그먼트
 *
 */
public class EventSetFragment extends BaseFragment {

    public static String EVENT_SET_OBJ = "event.set.obj";
    /**
     * http://milkissboy.tistory.com/34
     * @param eventSet
     * @return
     */
    public static EventSetFragment getInstance(EventSet eventSet) {
        EventSetFragment fragment = new EventSetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_SET_OBJ, eventSet);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    protected View initCOntentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.frag)
    }
}
