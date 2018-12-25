package com.example.android.mooddiary.diary.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.android.mooddiary.diary.db.DiaryDatabaseHelper;
import com.example.android.mooddiary.diary.utils.GetDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 有关日记的 Fragment
 *
 * Created by developerHaoz on 2017/5/2.
 */

public class DiaryFragment extends Fragment {

    @Bind(R.id.main_iv_circle)
    ImageView mIvCircle;
    @Bind(R.id.main_tv_date)
    TextView mTvDate;
    @Bind(R.id.main_tv_content)
    TextView mTvContent;
    @Bind(R.id.item_ll_control)
    LinearLayout mLlControl;
    @Bind(R.id.item_first)
    LinearLayout mItemFirst;
    @Bind(R.id.main_rv_show_diary)
    RecyclerView mRvShowDiary;
    @Bind(R.id.main_ll_main)
    LinearLayout mLlMain;
    @Bind(R.id.main_fab_enter_edit)
    FloatingActionButton mFabEnterEdit;
    @Bind(R.id.main_rl_main)
    RelativeLayout mRlMain;
    @Bind(R.id.main_ll_date)
    LinearLayout mLlDate;
    @Bind(R.id.main_ll_content)
    LinearLayout mLlContent;

    private List<Diary> mDiaryList;
    private DiaryDatabaseHelper mDatabaseHelper;
//    private Map<String,Integer> table =new TreeMap<>();//****************

    private static final String DB_DIARY_NAME = "Diary.db";

    public static DiaryFragment newInstance() {
        return new DiaryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);
        mDatabaseHelper = new DiaryDatabaseHelper(getActivity(), DB_DIARY_NAME, null, 1);
        getDiaryBeanList();
        initView();
        return view;
    }

    private void initView() {
        mTvDate.setText(GetDate.getDate());
        mRvShowDiary.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvShowDiary.setAdapter(new DiaryAdapter(getActivity(), mDiaryList));
    }

    private void getDiaryBeanList() {
        mDiaryList = new ArrayList<>();
        List<Diary> diaryList = new ArrayList<>();
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = sqliteDatabase.query("Diary", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {//如果今天没有写日记，提示写日记
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String dateSystem = GetDate.getDate().toString();
                if (date.equals(dateSystem)) {
                    mLlMain.removeView(mItemFirst);
                    break;
                }

            } while (cursor.moveToNext());
        }

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String tag = cursor.getString(cursor.getColumnIndex("tag"));
                int mood = cursor.getInt(cursor.getColumnIndex("mood"));
//                table.put(date,mood);//*************************
                mDiaryList.add(new Diary(date, title, content, tag, mood));

            } while (cursor.moveToNext());
        }
        cursor.close();

        for (int i = mDiaryList.size() - 1; i >= 0; i--) {
            diaryList.add(mDiaryList.get(i));
        }
        mDiaryList = diaryList;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDiaryBeanList();
        initView();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.main_fab_enter_edit)
    public void onViewClicked() {
        AddDiaryActivity.startActivity(getActivity());
    }

}

















