package com.example.android.mooddiary.common;

public class MoodBean {
    private String date;
    private int mood;

    public MoodBean(String date, int mood) {
        this.date = date;
        this.mood = mood;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }
}
