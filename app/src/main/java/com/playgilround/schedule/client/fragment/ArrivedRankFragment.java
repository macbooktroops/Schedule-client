package com.playgilround.schedule.client.fragment;

import android.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * 18-10-31
 * 도착 랭킹 화면 표시 프래그먼트
 * 도착 완료를 눌렀을 때 표시 됩니다.
 */
public class ArrivedRankFragment extends DialogFragment implements View.OnClickListener {

    static final String TAG = ArrivedRankFragment.class.getSimpleName();

    static ArrayList retName = new ArrayList();
    static ArrayList retArrived = new ArrayList();

    RecyclerView rvSchedule;


}
