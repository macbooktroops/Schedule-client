package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.common.base.app.BaseFragment;
import com.example.common.bean.Schedule;
import com.example.hyunwook.schedulermacbooktroops.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 18-06-16
 * 스케줄이 담기는 Adapter
 */
public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int SCHEDULE_TYPE = 1;
    private int SCHEDULE_CENTER = 2;
    private int SCHEDULE_FINISH_TYPE = 3;
    private int SCHEDULE_BOTTOM = 4;

    private Context mContext;
    private BaseFragment mBaseFragment;

    static final String TAG = ScheduleAdapter.class.getSimpleName();

    private List<Schedule> mSchedules;
    private List<Schedule> mFinishSchedules; //끝난 스케줄

    public ScheduleAdapter(Context context, BaseFragment baseFragment) {
        mContext = context;
        mBaseFragment = baseFragment;
        initData();
    }

    //Data array setting.
    private void initData() {
        mSchedules =  new ArrayList<>();
        mFinishSchedules = new ArrayList<>();
    }

    //ViewHolder생성
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SCHEDULE_TYPE) {
            return new ScheduleViewHolder
        }
    }


    //재활용 되는 뷰가 호출하여 실행되는 메소드,
    // 뷰 홀더를 전달하고 어댑터는 position 의 데이터를 결합
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScheduleViewHolder) {

        }
    }


    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {

        protected View vScheduleHintBlock;
        protected TextView tvScheduleState;
        protected TextView tvScheduleTitle;
        protected TextView tvScheduleTime;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            vScheduleHintBlock = itemView.findViewById(R.id.vSc)
        }
    }

}
