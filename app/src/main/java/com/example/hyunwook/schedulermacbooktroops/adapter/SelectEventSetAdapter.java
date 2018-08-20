package com.example.hyunwook.schedulermacbooktroops.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.common.bean.EventSet;
import com.example.common.realm.EventSetR;
import com.example.hyunwook.schedulermacbooktroops.R;

import java.util.List;

/**
 * 18-07-03
 * 스케줄 분류 저장된 데이터 어댑터
 */
public class SelectEventSetAdapter extends BaseAdapter {

    private Context mContext;
    private List<EventSetR> mEventSets;
    private int mSelectPosition;

    public SelectEventSetAdapter(Context context, List<EventSetR> eventSets, int selectPosition) {
        mContext = context;
        mEventSets = eventSets;
        mSelectPosition = selectPosition;
    }

    @Override
    public int getCount() {
        return mEventSets.size();
    }

    //item
    @Override
    public Object getItem(int position) {
        return mEventSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEventSets.get(position).getSeq();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        EventSetR eventSet = mEventSets.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_event_set, parent, false);
            holder.ivEvent = (ImageView) convertView.findViewById(R.id.ivEventSet);
            holder.tvEvent = (TextView) convertView.findViewById(R.id.tvEventSet);
            holder.rbEvent = (RadioButton) convertView.findViewById(R.id.rbEvent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvEvent.setText(eventSet.getName());
        //설정한 이미지라면 그 이미지 표시.
        holder.ivEvent.setImageResource(eventSet.getSeq() == 0 ?  R.mipmap.ic_select_event_set_category : R.mipmap.ic_select_event_set_icon);
        holder.rbEvent.setChecked(position == mSelectPosition);
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivEvent;
        private TextView tvEvent;
        private RadioButton rbEvent;

    }

    //선택 셋
    public void setSelectPosition (int selectPosition) {
        mSelectPosition = selectPosition;
        notifyDataSetChanged();
    }

    //선택 얻기
    public int getSelectPosition() {
        return mSelectPosition;
    }



}
