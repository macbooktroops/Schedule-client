package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.common.bean.EventSet;
import com.example.hyunwook.schedulermacbooktroops.R;
import com.example.hyunwook.schedulermacbooktroops.widget.SlideDeleteView;

import java.util.List;

/**
 * 스케줄 항목 이저장되는 Adapter
 */
public class EventSetAdapter extends RecyclerView.Adapter<EventSetAdapter.{

    private Context mContext;
    private List<EventSet> mEventSets;

    public EventSetAdapter(Context context, List<EventSet> eventSets) {
        mContext = context;
        mEventSets = eventSets;
    }

   //ViewHolder 추가
    protected class EventSetViewHolder extends RecyclerView.ViewHolder {

        private SlideDeleteView sdvEventSet;
        private View vEventSetColor;
        private TextView tvEventSetName;
        private ImageButton ibEventSetDelete;

        public EventSetViewHolder(View itemView) {
            super(itemView);

            sdvEventSet = (SlideDeleteView) itemView.findViewById(R.id.sdvE)
        }

   }
}
