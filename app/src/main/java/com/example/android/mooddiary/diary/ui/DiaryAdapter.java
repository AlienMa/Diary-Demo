package com.example.android.mooddiary.diary.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.android.mooddiary.R;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.event.StartUpdateDiaryEvent;
import com.example.android.mooddiary.diary.utils.GetDate;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 日记界面的 Adapter
 *
 * Created by developerHaoz on 2017/5/3.
 */

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Diary> mDiaryList;
    private int mEditPosition = -1;

    public static class DiaryViewHolder extends RecyclerView.ViewHolder{
        TextView mTvDate;
        TextView mTvTitle;
        TextView mTvContent;
        ImageView mIvEdit;
        LinearLayout mLlTitle;
        LinearLayout mLl;
        ImageView mIvCircle;
        LinearLayout mLlControl;
        RelativeLayout mRlEdit;

        DiaryViewHolder(View view){
            super(view);
            mIvCircle = (ImageView) view.findViewById(R.id.main_iv_circle);
            mTvDate = (TextView) view.findViewById(R.id.main_tv_date);
            mTvTitle = (TextView) view.findViewById(R.id.main_tv_title);
            mTvContent = (TextView) view.findViewById(R.id.main_tv_content);
            mIvEdit = (ImageView) view.findViewById(R.id.main_iv_edit);
            mLlTitle = (LinearLayout) view.findViewById(R.id.main_ll_title);
            mLl = (LinearLayout) view.findViewById(R.id.item_ll);
            mLlControl = (LinearLayout) view.findViewById(R.id.item_ll_control);
            mRlEdit = (RelativeLayout) view.findViewById(R.id.item_rl_edit);
        }
    }
//心情变化图片
    public static int SmileId(int processed){
        int id;
        double flag = processed*1.0/100;
        if(flag<0.1){
            id=R.drawable.sentiment_very_dissatisfied;
        }else if(flag>=0.1&&flag<0.2){
            id=R.drawable.sentiment_dissatisfied;
        }else if(flag>=0.2&&flag<0.4){
            id=R.drawable.mood_bad;
        }else if(flag>=0.4&&flag<0.6){
            id=R.drawable.sentiment_satisfied;
        }else if(flag>=0.6&&flag<0.8){
            id=R.drawable.mood;
        }else{
            id=R.drawable.sentiment_very_satisfied;
        }
        return id;
    }

    public DiaryAdapter(Context context, List<Diary> mDiaryList){
        mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDiaryList = mDiaryList;
    }
    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiaryViewHolder(mLayoutInflater.inflate(R.layout.item_rv_diary, parent, false));
    }

    @Override
    public void onBindViewHolder(final DiaryViewHolder holder, final int position) {

//        String dateSystem = GetDate.getDate().toString();
//        if(mDiaryList.get(position).getDate().equals(dateSystem)){
//            holder.mIvCircle.setImageResource(R.drawable.circle_orange);
//        }
        int id=SmileId(mDiaryList.get(position).getMood());
        holder.mIvCircle.setImageResource(id);//心情图片改变
        holder.mTvDate.setText(mDiaryList.get(position).getDate());
        holder.mTvTitle.setText(mDiaryList.get(position).getTitle());
        holder.mTvContent.setText("       " + mDiaryList.get(position).getContent());
        holder.mIvEdit.setVisibility(View.INVISIBLE);
        if(mEditPosition == position){
            holder.mIvEdit.setVisibility(View.VISIBLE);
        }else {
            holder.mIvEdit.setVisibility(View.GONE);
        }
        holder.mLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mIvEdit.getVisibility() == View.VISIBLE){
                    holder.mIvEdit.setVisibility(View.GONE);
                }else {
                    holder.mIvEdit.setVisibility(View.VISIBLE);
                }
                if(mEditPosition != position){
                    notifyItemChanged(mEditPosition);
                }
                mEditPosition = position;
            }
        });

        holder.mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new StartUpdateDiaryEvent(mDiaryList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiaryList.size();
    }

}
