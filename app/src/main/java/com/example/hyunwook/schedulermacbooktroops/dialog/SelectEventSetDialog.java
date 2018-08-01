package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.common.bean.EventSet;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.realm.EventSetR;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.AddEventSetActivity;
import com.example.hyunwook.schedulermacbooktroops.adapter.SelectEventSetAdapter;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.LoadEventSetTask;

import java.util.List;

import io.realm.RealmResults;

/**
 * 18-07-01
 * 스케줄 이벤트 설정 다이얼로그
 * DetailActivity
 */
public class SelectEventSetDialog extends Dialog implements View.OnClickListener, OnTaskFinishedListener<List<EventSetR>> {

    private Context mContext;
    private OnSelectEventSetListener mOnSelectEventSetListener;
    public static int ADD_EVENT_SET_CODE = 1;

    private int mId;

    private ListView lvEvent;
    private SelectEventSetAdapter mSelectEventSetAdapter;

//    private List<EventSet> mEventSets;
    RealmResults<EventSetR> mEventSets;

    public SelectEventSetDialog(Context context, OnSelectEventSetListener onSelectEventSetListener, int id) {
        super(context, R.style.DialogFullScreen);
        mContext = context;
        mOnSelectEventSetListener = onSelectEventSetListener;
        mId = id;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_select_event_set);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);
        findViewById(R.id.tvAddEventSet).setOnClickListener(this);

        lvEvent = (ListView) findViewById(R.id.lvEventSets);
        initData();

    }

    //저장된 이벤트 얻기
    private void initData() {
        new LoadEventSetTask(getContext(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectEventSetAdapter.setSelectPosition(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                if (mOnSelectEventSetListener != null) {
                    mOnSelectEventSetListener.onSelectEventSet(mEventSets.get(mSelectEventSetAdapter.getSelectPosition()));

                }

                dismiss();
                break;
            case R.id.tvAddEventSet:
                ((Activity) mContext).startActivityForResult(new Intent(mContext, AddEventSetActivity.class), ADD_EVENT_SET_CODE);
        }
    }

    //이벤트 추가
    public void addEventSet(EventSetR eventSet) {
        mEventSets.add(eventSet);
        mSelectEventSetAdapter.notifyDataSetChanged();
    }

    //작업이 끝나면
    @Override
    public void onTaskFinished(RealmResults<EventSetR> data) {
        mEventSets = data;

        EventSetR eventSet = new EventSetR();
        eventSet.setName(getContext().getString(R.string.menu_no_category));
        mEventSets.add(0, eventSet);

        int position = 0;
        for (int i = 0; i < mEventSets.size(); i++) {
            if (mEventSets.get(i).getId() == mId) {
                position = i;
                break;
            }
        }

        mSelectEventSetAdapter = new SelectEventSetAdapter(mContext, mEventSets, position);
        lvEvent.setAdapter(mSelectEventSetAdapter);
    }


    public interface OnSelectEventSetListener {
        void onSelectEventSet(EventSetR eventSet);
//        void onSelectEventSet(EventSet eventSet);
    }
}