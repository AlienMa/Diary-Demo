package com.example.android.mooddiary.diary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Diary {
    private String id;
    private String date;//日期
    private String title;//标题
    private String content;//内容
    private String tag;//标签
    private int mood;//

    public Diary(){
    }

    public Diary(String id,String date, String title, String content, String tag, int mood) {
        this.id=id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.mood = mood;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTag() {
        return tag;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId() + ".jpg";
    }

    public File getPhotoFile() {
//        File filesDir = mContext.getFilesDir();
        File filesDir = new File(Environment.getExternalStorageDirectory()+ "/MoodDiary/photos");
        if(!filesDir.exists()){
            filesDir.mkdirs();
        }
        return new File(filesDir, this.getPhotoFilename());
    }
}
