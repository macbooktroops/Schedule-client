package com.playgilround.schedule.client.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.holder.ChoiceHolder;
import com.playgilround.schedule.client.holder.FriendHolder;
import com.playgilround.schedule.client.utils.ScheduleFriendItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 18-10-20
 * 스케줄 저장 전 공유 친구 선택 어댑터
 */
public class ChoiceFriendAdapter extends RecyclerView.Adapter<ChoiceHolder> {

    static final String TAG = ChoiceFriendAdapter.class.getSimpleName();
    Context context;
//    ArrayList retName;
    private List<ScheduleFriendItem> itemModels;
    CardView friendCard;


    public ChoiceFriendAdapter(Context context, List<ScheduleFriendItem> name) {
        this.context = context;
        itemModels = name;
    }

    @Override
    public ChoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_choice_friend, parent, false);

        ChoiceHolder holder = new ChoiceHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChoiceHolder holder, int position) {
        ScheduleFriendItem item = itemModels.get(position);

        holder.userImage.setBackgroundResource(R.mipmap.ic_mainfriend);
        holder.userNickName.setText(item.getName());
        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(l -> {
            CheckBox cb = (CheckBox) l;
            int clickedPos = (Integer) cb.getTag();
            itemModels.get(clickedPos).setSelected(cb.isChecked()); //체크박스 체크 확인
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    //체크된 리스트 확인
    public List<ScheduleFriendItem> getSelectedItem() {
        List<ScheduleFriendItem> itemModelList = new ArrayList<>();

        for (int i = 0; i < itemModels.size(); i++) {
            ScheduleFriendItem itemModel = itemModels.get(i);
            if (itemModel.isSelected()) {
                itemModelList.add(itemModel);
            }
        }
        return itemModelList;
    }


}
