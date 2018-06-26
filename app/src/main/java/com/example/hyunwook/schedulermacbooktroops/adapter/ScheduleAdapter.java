package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.common.base.app.BaseFragment;
import com.example.common.bean.Schedule;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.utils.CalUtils;

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
            Log.d(TAG, "SCHEDULE_TYPE");
            return new ScheduleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule, parent, false));
        } else if (viewType == SCHEDULE_FINISH_TYPE) {
            Log.d(TAG, "SCHEDULE_FINISH_TYPE");
            return new ScheduleFinishViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule_finish, parent, false));
        } else if (viewType == SCHEDULE_CENTER) {
            Log.d(TAG, "SCHEDULE_CENTER");
            return new ScheduleCenterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule_center, parent, false));
        } else {
            Log.d(TAG, "SCHEDULE_BOTTOM");
            return new ScheduleBottomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule_bottom, parent, false));
        }
    }


    //재활용 되는 뷰가 호출하여 실행되는 메소드,
    // 뷰 홀더를 전달하고 어댑터는 position 의 데이터를 결합
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScheduleViewHolder) {
            final Schedule schedule = mSchedules.get(position);
            final ScheduleViewHolder viewHolder = (ScheduleViewHolder) holder;

            viewHolder.vScheduleHintBlock.setBackgroundResource();
        }
    }

    //schedule holder
    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {

        protected View vScheduleHintBlock;
        protected TextView tvScheduleState;
        protected TextView tvScheduleTitle;
        protected TextView tvScheduleTime;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            vScheduleHintBlock = itemView.findViewById(R.id.vScheduleHintBlock);
            tvScheduleState = (TextView) itemView.findViewById(R.id.tvScheduleState); //스케줄 완료여부
            tvScheduleTitle = (TextView) itemView.findViewById(R.id.tvScheduleTitle); //스케줄 이름
            tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime); //시간 표시부분
        }
    }

    @Override
    public int getItemCount() {
        return mSchedules.size() + mFinishSchedules.size() + 2;

    }

    //Finished Schedule
    protected class ScheduleFinishViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvChangeTaskList;
        protected TextView tvFinishHint;

        public ScheduleFinishViewHolder(View itemView) {
            super(itemView);
            tvChangeTaskList = (TextView) itemView.findViewById(R.id.tvChangeTaskList);
            tvFinishHint = (TextView)  itemView.findViewById(R.id.tvFinishHint);
        }

    }

    protected class ScheduleCenterViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvChangeTaskList;
        protected TextView tvFinishHint;

        public ScheduleCenterViewHolder(View itemView) {
            super(itemView);
            tvChangeTaskList = (TextView) itemView.findViewById(R.id.tvChangeTaskList);
            tvFinishHint = (TextView) itemView.findViewById(R.id.tvFinishHint);
        }
    }

    protected class ScheduleBottomViewHolder extends RecyclerView.ViewHolder {

        public ScheduleBottomViewHolder(View itemView) {
            super(itemView);
        }
    }




}
