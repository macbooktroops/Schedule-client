package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.app.BaseFragment;
import com.example.common.bean.Schedule;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.ScheduleDetailActivity;
import com.example.hyunwook.schedulermacbooktroops.dialog.ConfirmDialog;
import com.example.hyunwook.schedulermacbooktroops.fragment.ScheduleFragment;
import com.example.hyunwook.schedulermacbooktroops.task.schedule.RemoveScheduleTask;
import com.example.hyunwook.schedulermacbooktroops.task.schedule.UpdateScheduleTask;
import com.example.hyunwook.schedulermacbooktroops.utils.CalUtils;
import com.example.hyunwook.schedulermacbooktroops.widget.StrikeThruTextView;

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

    private boolean mIsShowFinishTask = false;

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

            viewHolder.vScheduleHintBlock.setBackgroundResource(CalUtils.getScheduleBlockView(schedule.getColor()));;
            viewHolder.tvScheduleTitle.setText(schedule.getTitle());

            //저장된 데이터 시간검사.
            if (schedule.getTime() != 0) {
                viewHolder.tvScheduleTime.setText(CalUtils.timeStamp2Time(schedule.getTime()));
            } else {
                viewHolder.tvScheduleTime.setText("");
            }

            if (schedule.getState() == 0) {
                viewHolder.tvScheduleState.setBackgroundResource(R.drawable.start_schedule_hint);
                viewHolder.tvScheduleState.setText(mContext.getString(R.string.start));
                viewHolder.tvScheduleState.setTextColor(mContext.getResources().getColor(R.color.color_schedule_start));
            } else {
                viewHolder.tvScheduleState.setBackgroundResource(R.drawable.finish_schedule_hint);
                viewHolder.tvScheduleState.setText(mContext.getString(R.string.finish));
                viewHolder.tvScheduleState.setTextColor(mContext.getResources().getColor(R.color.color_schedule_finish));
            }

            //상태 클릭시 (시작 , 종료)
            viewHolder.tvScheduleState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeScheduleState(schedule);
                }
            });

            //스케줄 자체클릭시 디테일 액티비티
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBaseFragment instanceof ScheduleFragment) {
                        mContext.startActivity(new Intent(mContext, ScheduleDetailActivity.class)
                                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule)
                                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION, ((ScheduleFragment) mBaseFragment).getCurrentCalendarPosition()));

                    } else {
                        mContext.startActivity(new Intent(mContext, ScheduleDetailActivity.class)
                                .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule)
                                .putExtra(ScheduleDetailActivity.CALENDAR_POSITION,  -1));
                    }
                }
            });
        } else if (holder instanceof ScheduleFinishViewHolder) {
            final Schedule schedule = mFinishSchedules.get(position - mSchedules.size() -1);
            ScheduleFinishViewHolder viewHolder = (ScheduleFinishViewHolder) holder;

            viewHolder.tvScheduleTitle.setText(schedule.getTitle());

            if (mIsShowFinishTask) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.space_3dp);
                viewHolder.itemView.setLayoutParams(params);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                params.height = 0;
                params.bottomMargin = 0;
                viewHolder.itemView.setLayoutParams(params);
            }

            //finish 상태에서 다시 클릭 시 삭제.
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDeleteScheduleDialog(schedule);
                }
            });
            //완료된 내용들보기.
        } else if (holder instanceof ScheduleCenterViewHolder) {
            ScheduleCenterViewHolder viewHolder = (ScheduleCenterViewHolder) holder;
            if (mFinishSchedules.size() > 0) {
                viewHolder.tvChangeTaskList.setEnabled(true);
            } else {
                viewHolder.tvChangeTaskList.setEnabled(false);
            }

            viewHolder.tvChangeTaskList.setText(mIsShowFinishTask ? mContext.getString(R.string.schedule_hide_finish_task) : mContext.getString(R.string.schedule_show_finish_task));
            viewHolder.tvFinishHint.setVisibility(mIsShowFinishTask && mFinishSchedules.size() > 0 ? View.VISIBLE : View.GONE);
            viewHolder.tvChangeTaskList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsShowFinishTask = !mIsShowFinishTask;
                    //완료된 게 오픈인지 아닌지.
                    notifyDataSetChanged();
                }
            });
        }
    }

    //일정을 삭제하시겠습니까? 다이얼로그
    private void showDeleteScheduleDialog(final Schedule schedule) {
        new ConfirmDialog(mContext, R.string.schedule_delete_this_schedule, new ConfirmDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                //확인버튼
                new RemoveScheduleTask(mContext, new OnTaskFinishedListener<Boolean>() {
                    @Override
                    public void onTaskFinished(Boolean data) {
                        //작업이 끝나면
                        if (data) {
                            removeItem(schedule);
                            if (mBaseFragment instanceof ScheduleFragment) {
                                ((ScheduleFragment) mBaseFragment).resetScheduleList();
                            }
                        }
                    }
                }, schedule.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).show();
    }

    public void removeItem(Schedule schedule) {
        if (mSchedules.remove(schedule)) {
            notifyDataSetChanged(); //reupdate.
        } else if (mFinishSchedules.remove(schedule)) {
            notifyDataSetChanged();
        }
    }

    //추가
    public void insertItem(Schedule schedule) {
        mSchedules.add(schedule);
        notifyItemInserted(mSchedules.size() -1);
    }

    //변경
    public void changeAllData(List<Schedule> schedules) {
        distin
    }

    //데이터 구별
    private void distinguishData(List<Schedule> schedules) {
        mSchedules.clear();
        mFinishSchedules.clear();

        for (int i =0, count = schedules.size(); i < count; i++) {
            Schedule schedule = schedules.get(i);

            //상태 2면 끝난 스케줄
            if (schedule.getState() == 2) {
                mFinishSchedules.add(schedule);
            } else {
                mSchedules.add(schedule);
            }
        }
        notifyDataSetChanged();
    }
   //스케줄 상태 변경
    private void changeScheduleState(final Schedule schedule) {
        switch (schedule.getState()) {
            //start --> finish
            case 0:
                schedule.setState(1); //0 --> 1
                new UpdateScheduleTask(mContext, new OnTaskFinishedListener<Boolean>() {
                    @Override
                    public void onTaskFinished(Boolean data) {
                        changeScheduleItem(schedule);
                    }
                }, schedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 1:
                //finish -> real finish
                schedule.setState(2);
                new UpdateScheduleTask(mContext, new OnTaskFinishedListener<Boolean>() {
                    @Override
                    public void onTaskFinished(Boolean data) {
                        mSchedules.remove(schedule);
                        mFinishSchedules.add(schedule);
                        notifyDataSetChanged();
                    }
                }, schedule).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }



    private void changeScheduleItem(Schedule schedule) {
        int i = mSchedules.indexOf(schedule);
        Log.d(TAG, "changeSchedule --->" + i);
        if (i != -1) {
            notifyDataSetChanged();
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

        protected StrikeThruTextView tvScheduleTitle;
        protected TextView tvScheduleTime;


        public ScheduleFinishViewHolder(View itemView) {
            super(itemView);
            tvScheduleTitle = (StrikeThruTextView) itemView.findViewById(R.id.tvScheduleTitle);
            tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime);
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
