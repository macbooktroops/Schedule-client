package com.example.hyunwook.schedulermacbooktroops.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.common.bean.EventSet;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.LoadEventSetTask;

import java.util.List;

/**
 * 18-07-01
 * 스케줄 이벤트 설정 다이얼로그
 * DetailActivity
 */
public class SelectEventSetDialog extends Dialog implements View.OnClickListener, OnTaskFinishedListener<List<EventSet>>{

    private Context mContext;
    private OnSelectEventSetListener mOnSelectEventSetListener;

    private int mId;

    private ListView lvEvent;
    private SelectEventSetAdapter
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
                mSelect
            }
        });
    }
}

public interface OnSelectEventSetListener {
    void onSelectEventSet(EventSet eventSet);
}
