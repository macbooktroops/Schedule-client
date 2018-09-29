package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.playgilround.common.listener.OnTaskFinishedListener;
import com.playgilround.common.realm.EventSetR;
import com.playgilround.common.realm.ScheduleR;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.MainActivity;
import com.playgilround.schedule.client.dialog.ConfirmDialog;
import com.playgilround.schedule.client.task.eventset.RemoveEventSetRTask;
import com.playgilround.schedule.client.utils.CalUtils;
import com.playgilround.schedule.client.widget.SlideDeleteView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * 스케줄 항목 이저장되는 Adapter
 */
public class EventSetAdapter extends RecyclerView.Adapter<EventSetAdapter.EventSetViewHolder> {

    static final String TAG = EventSetAdapter.class.getSimpleName();

    private Context mContext;
    private List<EventSetR> mEventSets;
    Realm realm;

    public EventSetAdapter(Context context, List<EventSetR> eventSets) {
        realm = Realm.getDefaultInstance();
        mContext = context;
        mEventSets = eventSets;
    }

    /**
     * http://i5on9i.blogspot.com/2015/03/baseadapter-recyclerviewadapter.html
     * onCreateViewHolder 는 정말 새롭게 생성될 때에만 불린다
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public EventSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_set, parent, false);
        return new EventSetViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mEventSets.size();
    }

    @Override
    public void onBindViewHolder(EventSetViewHolder holder, final int position) {
//        holder.bind(position);
        final EventSetR eventSet = mEventSets.get(position);
        holder.sdvEventSet.close(false);
        holder.tvEventSetName.setText(eventSet.getName()); //name
        holder.vEventSetColor.setBackgroundResource(CalUtils.getEventSetColor(eventSet.getColor()));
        holder.ibEventSetDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //휴지통 삭제버튼 클릭 시
                Log.d(TAG, "onClick delete TestSchedule2 -->" + eventSet.getSeq());

                if (eventSet.getSeq() == -1) {
                    Toast.makeText(mContext, "공휴일 일정은 삭제가 불가능해요.", Toast.LENGTH_LONG).show();
                } else {
                    showDeleteEventSetDialog(eventSet, position);
                }
            }
        });

        holder.sdvEventSet.setOnContentClickListener(new SlideDeleteView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                Log.d(TAG, "onContentClick ----->" +eventSet.getName());
                gotoEventSetFragment(eventSet); //해당 스케줄 분류 프레그먼트로.
            }
        });
    }

    private void showDeleteEventSetDialog(final EventSetR eventSet, final int position) {
        Log.d(TAG, "showDeleteEvent--");
        new ConfirmDialog(mContext, R.string.event_set_delete_this_event_set, new ConfirmDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.d(TAG, "try eventsetAdapter ");
                        RealmResults<ScheduleR> scheduleR = realm.where(ScheduleR.class)
                                .equalTo("eventSetId", eventSet.getSeq()).findAll();

                        Log.d(TAG, "remove task schedule ->" + scheduleR.size());

//                        for (int i = 0; i < scheduleR.size(); i++ ) {
                            for (ScheduleR schedule : scheduleR) {
                                schedule.setEventSetId(0);
                            }

                        SystemClock.sleep(100);

                        Log.d(TAG, "try delete eventset");
                        removeItem(position);
                        eventSet.deleteFromRealm();
//                            }
                    }
                });
            }
        }).show();
              /*  new RemoveEventSetRTask(mContext, new OnTaskFinishedListener<Boolean>() {
                    @Override
                    public void onTaskFinished(Boolean data) {
                        if (data) {
                            removeItem(position);
                        }
                    }
                }, eventSet.getSeq(), realm).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); //병렬처리*/

    }

    private void gotoEventSetFragment(EventSetR eventSet) {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).gotoEventSetFragment(eventSet);
        }
    }

    //삭제 아이템
    public void removeItem(int position) {
        mEventSets.remove(position);
        notifyDataSetChanged();
    }

    //초기화 후 데이터 체인지
    public void changeAllData(List<EventSetR> eventSets) {
        mEventSets.clear();
        mEventSets.addAll(eventSets);
        notifyDataSetChanged();
    }

    public void insertItem(EventSetR eventSet) {
        mEventSets.add(eventSet);
        notifyItemInserted(mEventSets.size() - 1);
        Log.d("insertData", "mevent -->" + mEventSets.get(mEventSets.size() - 1).getName());
        //리스트 맨끝에 데이터 추가
    }

    //ViewHolder 추가
    class EventSetViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, SlideDeleteView.OnContentClickListener {

        @BindView(R.id.sdvEventSet)
        SlideDeleteView sdvEventSet;
        @BindView(R.id.vEventSetColor)
        View vEventSetColor;
        @BindView(R.id.tvEventSetName)
        TextView tvEventSetName;
        @BindView(R.id.ibEventSetDelete)
        ImageButton ibEventSetDelete;

        EventSetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            EventSetR eventSet = mEventSets.get(position);
            if (eventSet != null) {
                sdvEventSet.close(false);

                tvEventSetName.setText(eventSet.getName());
                vEventSetColor.setBackgroundResource(CalUtils.getEventSetColor(eventSet.getColor()));

                ibEventSetDelete.setTag(position);
                ibEventSetDelete.setOnClickListener(this);

                sdvEventSet.setTag(position);
                sdvEventSet.setOnContentClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            //휴지통 삭제버튼 클릭 시
            int position = (int) view.getTag();
            EventSetR eventSet = mEventSets.get(position);

            if (view.getId() == R.id.ibEventSetDelete) {
                Log.d(TAG, "onClick delete TestSchedule");
                showDeleteEventSetDialog(eventSet, position);
            }
        }

        @Override
        public void onContentClick() {
            // 해당 스케줄 분류 프레그먼트로.
            int position = (int) sdvEventSet.getTag();
            EventSetR eventSet = mEventSets.get(position);
            Log.d(TAG, "onContentClick 2----->" +eventSet.getName());

            gotoEventSetFragment(eventSet);
        }
    }
}
