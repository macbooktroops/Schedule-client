package com.playgilround.schedule.client.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.base.app.BaseActivity;
import com.playgilround.schedule.client.dialog.SelectColorDialog;
import com.playgilround.schedule.client.listener.OnTaskFinishedListener;
import com.playgilround.schedule.client.realm.EventSetR;
import com.playgilround.schedule.client.task.eventset.AddEventSetRTask;
import com.playgilround.schedule.client.utils.CalUtils;
import com.playgilround.schedule.client.utils.ToastUtils;

import io.realm.Realm;

/**
 * 18-06-19
 * 스케줄 분류 추가 액티비티
 */
public class AddEventSetActivity extends BaseActivity implements View.OnClickListener,  SelectColorDialog.OnSelectColorListener {

    private EditText etEventSetName;
    private View vEventSetColor;
    private SelectColorDialog mSelectColorDialog;

    public static int ADD_EVENT_SET_CANCEL = 1;
    public static int ADD_EVENT_SET_FINISH = 2;
    public static String EVENT_SET_OBJ = "event.set.obj";

    static final String TAG = AddEventSetActivity.class.getSimpleName();
    private int mColor = 0;

    Realm realm;

    @Override
    protected void bindView() {
        setContentView(R.layout.activity_add_event_set);


        realm = Realm.getDefaultInstance();
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
                break;
            case R.id.rlEventSetColor:
                Log.d(TAG, "rlEventSetColor ---");
                showSelectColorDialog();
                break;
        }
    }

    //스케줄 분류 중요도 색상 다이얼로그
    private void showSelectColorDialog() {
        if (mSelectColorDialog == null)
            mSelectColorDialog = new SelectColorDialog(this, this);


        mSelectColorDialog.show();
    }

    //스케줄 분류 항목 추가
    private void addEventSet() {
        final String name = etEventSetName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortToast(this, R.string.event_set_name_is_not_null);
        } else {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Number currentIdNum = realm.where(EventSetR.class).max("seq");
                    Log.d(TAG, "Try Event Set --->" + currentIdNum);
                    int nextId;

                    if (currentIdNum.intValue() == -1) {
                        Log.d(TAG, "init Event");
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    EventSetR eventSet = realm.createObject(EventSetR.class, nextId);
                    eventSet.setName(name);
                    eventSet.setColor(mColor);
                    new AddEventSetRTask(getApplicationContext(), new OnTaskFinishedListener<EventSetR>() {
                        @Override
                        public void onTaskFinished(EventSetR data) {
                            Log.d(TAG, "AddEventSetRTask task finish -->"+data.getName());
                            /**
                             * Intent 전달 시
                             * EventSetR에 seq 전달로 통일.
                             */
                            setResult(ADD_EVENT_SET_FINISH, new Intent().putExtra(EVENT_SET_OBJ, data.getSeq()));
//                            setResult(ADD_EVENT_SET_FINISH, new Intent(EVENT_SET_OBJ));
                            finish();
                        }
                    }, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });

            /*EventSet eventSet = new EventSet();
            eventSet.setName(name);
            eventSet.setColor(mColor);
            new AddEventSetTask(this, this, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
        }
    }

    //색깔 선택 다이얼로그에서 Confirm버튼 누르면 호출
    @Override
    public void onSelectColor(int color) {
        //SelectColorDialog 접근 시, 색상 선택안하고 확인눌렀을 경우 기본 빨강.
        if (color == 0) {
            mColor = 1;
            color = 1;
        } else {
            mColor = color;
        }
        Log.d(TAG, "onSelectColor -->" + mColor);
        vEventSetColor.setBackgroundResource(CalUtils.getEventSetCircle(color));
    }
}
