package com.example.hyunwook.schedulermacbooktroops.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.common.base.app.BaseActivity;
import com.example.common.bean.EventSet;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.common.util.ToastUtils;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.dialog.SelectColorDialog;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.AddEventSetTask;
import com.example.hyunwook.schedulermacbooktroops.utils.CalUtils;

/**
 * 18-06-19
 * 스케줄 분류 추가 액티비티
 */
public class AddEventSetActivity extends BaseActivity implements View.OnClickListener, OnTaskFinishedListener<EventSet>, SelectColorDialog.OnSelectColorListener {

    private EditText etEventSetName;
    private View vEventSetColor;
    private SelectColorDialog mSelectColorDialog;

    public static int ADD_EVENT_SET_CANCEL = 1;
    public static int ADD_EVENT_SET_FINISH = 2;
    public static String EVENT_SET_OBJ = "event.set.obj";


    private int mColor = 0;
    @Override
    protected void bindView() {
        setContentView(R.layout.activity_add_event_set);

        TextView tvTitle = searchViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.menu_add_event_set));

        etEventSetName = searchViewById(R.id.etEventSetName);
        vEventSetColor = searchViewById(R.id.vEventSetColor);

        searchViewById(R.id.tvCancel).setOnClickListener(this);
        searchViewById(R.id.tvFinish).setOnClickListener(this);
        searchViewById(R.id.rlEventSetColor).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                setResult(ADD_EVENT_SET_CANCEL);
                finish();
                break;
            case R.id.tvFinish:
                addEventSet();
            case R.id.rlEventSetColor:
                showSelectColorDialog();
                break;
        }
    }

    //스케줄 분류 중요도 색상 다이얼로그
    private void showSelectColorDialog() {
        if (mSelectColorDialog == null) {
            mSelectColorDialog = new SelectColorDialog(this, this);
        }

        mSelectColorDialog.show();
    }

    //스케줄 분류 항목 추가
    private void addEventSet() {
        String name = etEventSetName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortToast(this, R.string.event_set_name_is_not_null);
        } else {
            EventSet eventSet = new EventSet();
            eventSet.setName(name);
            eventSet.setColor(mColor);
            new AddEventSetTask(this, this, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    //작업이 끝났다고 전달
    @Override
    public void onTaskFinished(EventSet data) {
        setResult(ADD_EVENT_SET_FINISH, new Intent().putExtra(EVENT_SET_OBJ, data));
        finish();
    }

    //색깔 선택 다이얼로그에서 Confirm버튼 누르면 호출
    @Override
    public void onSelectColor(int color) {
        mColor = color;
        vEventSetColor.setBackgroundResource(CalUtils.getEventSetCircle(color));
    }
}
