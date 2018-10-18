package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.common.realm.ScheduleR;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.dialog.ConfirmDialog;
import com.playgilround.schedule.client.utils.CalUtils;
import com.playgilround.schedule.client.widget.StrikeThruTextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Retrofit;

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
    private ScheduleEvent mEvent;
//    private BaseFragment mBaseFragment;

    static final String TAG = ScheduleAdapter.class.getSimpleName();

    private List<ScheduleR> mSchedules;
    private List<ScheduleR> mFinishSchedules; //끝난 스케줄

    private boolean mIsShowFinishTask = false;

    /**
     * Object is no longer managed by Realm. Has it been delete? 회피하기위해
     * 복사본 생성
     */
    ScheduleR remSchedule;
    Realm realm;

    public ScheduleAdapter(Context context, ScheduleEvent event) {
//    public ScheduleAdapter(Context context, BaseFragment baseFragment) {
        realm = Realm.getDefaultInstance();
        mContext = context;
        mEvent = event;
//        mBaseFragment = baseFragment;
        initData();
    }

    //Data array setting.
    private void initData() {
        mSchedules =  new ArrayList<>(); //진행중인 스케줄
        mFinishSchedules = new ArrayList<>(); //완료된 스케줄
    }

    //ViewHolder생성
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreate state -->" + viewType);
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
            final ScheduleR schedule = mSchedules.get(position);
            final ScheduleViewHolder viewHolder = (ScheduleViewHolder) holder;

            Log.d(TAG, "Schedule bind --> " +schedule.getTitle());

            Log.d(TAG, "schedule eventId ->" + schedule.getEventSetId());

            //EventSetId = 0 ->  기본 스케줄 분류 일경우에는 회색 표시.
      /*      if (schedule.getEventSetId() == 0) {
                Log.d(TAG, "this event basic---");
                viewHolder.vScheduleHintBlock.setBackgroundColor(Color.GRAY);
                viewHolder.tvScheduleTitle.setText(schedule.getTitle());
            } else {*/
                viewHolder.vScheduleHintBlock.setBackgroundResource(CalUtils.getScheduleBlockView(schedule.getColor()));
                viewHolder.tvScheduleTitle.setText(schedule.getTitle());
//            }
            //저장된 데이터 시간검사.
            if (schedule.getTime() != 0) {
                viewHolder.tvScheduleTime.setText(CalUtils.timeStamp2Time(schedule.getTime()));
            } else {
                viewHolder.tvScheduleTime.setText("");
            }

            if (schedule.getState() == 0) {
                Log.d(TAG, "State 0");
                viewHolder.tvScheduleState.setBackgroundResource(R.drawable.start_schedule_hint);
                viewHolder.tvScheduleState.setText(mContext.getString(R.string.start));
                viewHolder.tvScheduleState.setTextColor(mContext.getResources().getColor(R.color.color_schedule_start));
            } else if (schedule.getState() == 1){
                Log.d(TAG, "State 1");
                viewHolder.tvScheduleState.setBackgroundResource(R.drawable.finish_schedule_hint);
                viewHolder.tvScheduleState.setText(mContext.getString(R.string.finish));
                viewHolder.tvScheduleState.setTextColor(mContext.getResources().getColor(R.color.color_schedule_finish));
            } else if (schedule.getState() == -1) {
                Log.d(TAG, "State -1");
                viewHolder.tvScheduleState.setBackgroundResource(R.drawable.holiday_schedule_hint);
                viewHolder.tvScheduleState.setText(mContext.getString(R.string.holiday));
                viewHolder.tvScheduleState.setTextColor(mContext.getResources().getColor(R.color.color_holiday_view));

            }

            //상태 클릭시 (시작 , 종료)
            viewHolder.tvScheduleState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "change schedule --> " +schedule.getTitle());
                    changeScheduleState(schedule);
                }
            });

            //스케줄 자체클릭시 디테일 액티비티
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mEvent.onClick(schedule);

                }
            });
        } else if (holder instanceof ScheduleFinishViewHolder) {
            final ScheduleR schedule = mFinishSchedules.get(position - mSchedules.size() -1);
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
                    Log.d(TAG, "delete schedule");
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
    private void showDeleteScheduleDialog(final ScheduleR schedule) {
        new ConfirmDialog(mContext, R.string.schedule_delete_this_schedule, new ConfirmDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                //확인버튼
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "success delete");
                                removeItem(schedule);
                              /*  if (mSchedules.remove(schedule)) {
                                    notifyDataSetChanged(); //reupdate.
                                } else if (mFinishSchedules.remove(schedule)) {
                                    notifyDataSetChanged();
                                }*/
                                schedule.deleteFromRealm();
                                mEvent.onReset();


                            }
                        });
                    //}


                        //}
                        //});
                        //작업이 끝나면
                        //}
                        //}, schedule)./**/executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
/*
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        schedule.deleteFromRealm();
                        Log.d(TAG, "delete success.");

                        removeItem(schedule);
                        mEvent.onReset();
                    }
                });*/

            }
        }).show();
    }

    //getItemViewType을 호출해서 현재 position의 viewType얻기
    @Override
    public int getItemViewType(int position) {
        if (position < mSchedules.size()) {
            return SCHEDULE_TYPE;
        } else if (position == mSchedules.size()) {
            return SCHEDULE_CENTER;
        } else if (position == getItemCount() -1) {
            return SCHEDULE_BOTTOM;
        } else {
            return SCHEDULE_FINISH_TYPE;
        }
    }

    public void removeItem(ScheduleR schedule) {
        //Log.d(TAG,"removeItem -->" + schedule.getTitle());
        if (mSchedules.remove(schedule)) {
            notifyDataSetChanged(); //reupdate.
        } else if (mFinishSchedules.remove(schedule)) {
            notifyDataSetChanged();
        }
    }

    //추가
    public void insertItem(ScheduleR schedule) {
        Log.d(TAG, "schedule ==>" + schedule.getTitle());
        mSchedules.add(schedule);
        notifyItemInserted(mSchedules.size() -1);
    }

    //변경
    public void changeAllData(List<ScheduleR> schedules) {
        distinguishData(schedules);
    }

    //데이터 구별
    private void distinguishData(List<ScheduleR> schedules) {
        mSchedules.clear();
        mFinishSchedules.clear();

        for (int i =0, count = schedules.size(); i < count; i++) {
            ScheduleR schedule = schedules.get(i);

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
    private void changeScheduleState(final ScheduleR schedule) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                switch (schedule.getState()) {
                    //start --> finish
                    case 0:
                        Log.d(TAG, "change schedule -->" + schedule.getTitle());
                        schedule.setState(1); //0 --> 1

                        changeScheduleItem(schedule);

                        break;
                    case 1:
                        //finish -> real finish
                        Log.d(TAG, "change schedule finish -->" + schedule.getTitle());
                        schedule.setState(2);
                        mSchedules.remove(schedule);
                        mFinishSchedules.add(schedule);
                        notifyDataSetChanged();

                        break;

                    case -1:
                        Log.d(TAG, "holiday check.");
                }
            }
        });
    }

    //서버에 스케줄 업데이트
    /**
     * {
     *     "title": "오늘창업허브",
     *     "state" : 0,
     *     "start_time": "2018-10-01 13:00:00",
     *     "content": "내용입니다.",
     *     "latitude": 37.6237604,
     *     "longitude": 126.9218479
     * }
     * @param schedule
     */
    private void changeScheduleItem(ScheduleR schedule) {
        int i = mSchedules.indexOf(schedule);
        Log.d(TAG, "changeSchedule --->" + i);

        String title = schedule.getTitle();
        int state = schedule.getState();
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int day = schedule.getDay();
        String hTime = schedule.gethTime();
        String content = schedule.getDesc();
        long latitude = schedule.getLatitude();
        long longitude = schedule.getLongitude();

        Log.d(TAG, "change title -->" + title);
        Log.d(TAG, "change state -->" + state);
        Log.d(TAG, "change year -->" + year);
        Log.d(TAG, "change month -->" + month);
        Log.d(TAG, "change day -->" + day);
        Log.d(TAG, "change hTime -->" + hTime);
        Log.d(TAG, "change content -->" + content);
        Log.d(TAG, "change latitude -->" + latitude);
        Log.d(TAG, "change longitude -->" + longitude);


        if (i != -1) {
            notifyDataSetChanged();
        }
/*
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("state", 1);

        if (hTime.equals("0") || hTime == null) {
            Log.d(TAG, "hTime is 0 ");
            hTime = "00:00:00";
            jsonObject.addProperty("start_time", year + "-" + month + "-" + day+ " "+ hTime);
        } else {
            jsonObject.addProperty("start_time", year + "-" + month + "-" + day+ " "+ hTime);
        }
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);

        Log.d(TAG, "change jsonObject -->" + jsonObject);*/

/*        Retrofit retrofit = APIClient.getClient();
        APIInterface postUpdateSche = retrofit.create(APIInterface.class);
        Call<JsonObject> result = postUpdateSche.postUpdateSchedule()*/
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

    public interface ScheduleEvent {
        void onClick(ScheduleR schedule);
        void onReset();
    }
}
