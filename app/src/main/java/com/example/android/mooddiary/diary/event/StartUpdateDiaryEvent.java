package com.example.android.mooddiary.diary.event;

import com.example.android.mooddiary.diary.utils.Diary;

/**
 * 打开「修改日记」的界面
 *
 * Created by developerHaoz on 2017/5/3.
 */

public class StartUpdateDiaryEvent {

    private Diary mDiary;

    public StartUpdateDiaryEvent(Diary diary) {
        mDiary = diary;
    }

    public Diary getDiary() {
        return mDiary;
    }
}
