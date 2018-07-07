package com.example.hyunwook.schedulermacbooktroops.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.common.base.app.BaseActivity;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.dialog.SelectColorDialog;

/**
 * 18-06-19
 * 스케줄 분류 추가 액티비티
 */
public class AddEventSetActivity extends BaseActivity implements View.OnClickListener  {

    private EditText etEventSetName;
    private View vEventSetColor;
    private SelectColorDialog mSelectColorDialog;

    @Override
    protected void bindView() {
        setContentView(R.layout.activity_add_event_set);

        TextView tvTitle = searchViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.menu_add_event_set));


    }
}
