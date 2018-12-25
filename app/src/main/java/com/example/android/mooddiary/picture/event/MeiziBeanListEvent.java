package com.example.android.mooddiary.picture.event;

import com.example.android.mooddiary.picture.bean.MeiziBean;

import java.util.List;

/**
 * 包含妹子信息的 Event
 *
 * Created by developerHaoz on 2017/5/3.
 */

public class MeiziBeanListEvent {

    private List<MeiziBean> mMeiziBeanList;

    public MeiziBeanListEvent(List<MeiziBean> meiziBeanList) {
        mMeiziBeanList = meiziBeanList;
    }
    public List<MeiziBean> getMeiziBeanList() {
        return mMeiziBeanList;
    }

}
