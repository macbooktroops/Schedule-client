package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.common.bean.EventSet;
import com.example.common.listener.OnTaskFinishedListener;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.activity.MainActivity;
import com.example.hyunwook.schedulermacbooktroops.dialog.ConfirmDialog;
import com.example.hyunwook.schedulermacbooktroops.task.eventset.RemoveEventSetTask;
import com.example.hyunwook.schedulermacbooktroops.utils.CalUtils;
import com.example.hyunwook.schedulermacbooktroops.widget.SlideDeleteView;

import java.util.List;

/**
 * 스케줄 항목 이저장되는 Adapter
 */
public class EventSetAdapter extends RecyclerView.Adapter<EventSetAdapter.EventSetViewHolder>{


    private Context mContext;
    private List<EventSet> mEventSets;

    public EventSetAdapter(Context context, List<EventSet> eventSets) {
        mContext = context;
        mEventSets = eventSets;
    }

    @Override
    public EventSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventSetViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_event_set, parent, false));
    }

    @Override
    public int getItemCount() {
        return mEventSets.size();
    }

    @Override
    public void onBindViewHolder(EventSetViewHolder holder, final int position) {
        final EventSet eventSet = mEventSets.get(position);
        holder.sdvEventSet.close(false);
        holder.tvEventSetName.setText(eventSet.getName()); //name
        holder.vEventSetColor.setBackgroundResource(CalUtils.getEventSetColor(eventSet.getColor()));
        holder.ibEventSetDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //휴지통 삭제버튼 클릭 시
                showDeleteEventSetDialog(eventSet, position);
            }
        });

        holder.sdvEventSet.setOnContentClickListener(new SlideDeleteView.OnContentClickListener() {
            @Override
            public void onContentClick() {
                gotoEventSetFragment(eventSet); //해당 스케줄 분류 프레그먼트로.
            }
        });

    }

    private void showDeleteEventSetDialog(final EventSet eventSet, final int position) {
        new ConfirmDialog(mContext, R.string.event_set_delete_this_event_set, new ConfirmDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                new RemoveEventSetTask(mContext, new OnTaskFinishedListener<Boolean>() {
                    @Override
                    public void onTaskFinished(Boolean data) {
                        if (data) {
                            removeItem(position);
                        }
                    }
                }, eventSet.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).show();
    }

    private void gotoEventSetFragment(EventSet eventSet) {
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
    public void changeAllData(List<EventSet> eventSets) {
        mEventSets.clear();
        mEventSets.addAll(eventSets);
        notifyDataSetChanged();
    }

    public void insertItem(EventSet eventSet) {
        mEventSets.add(eventSet);
        notifyItemInserted(mEventSets.size() -1);

        //리스트 맨끝에 데이터 추가
    }

   //ViewHolder 추가
    protected class EventSetViewHolder extends RecyclerView.ViewHolder {

        private SlideDeleteView sdvEventSet;
        private View vEventSetColor;
        private TextView tvEventSetName;
        private ImageButton ibEventSetDelete;

        public EventSetViewHolder(View itemView) {
            super(itemView);

            sdvEventSet = (SlideDeleteView) itemView.findViewById(R.id.sdvEventSet);
            vEventSetColor = itemView.findViewById(R.id.vEventSetColor);
            tvEventSetName = (TextView) itemView.findViewById(R.id.tvEventSetName);
            ibEventSetDelete = (ImageButton) itemView.findViewById(R.id.ibEventSetDelete);
        }

   }
}
