package com.example.android.mooddiary.diary.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.mooddiary.R;
import com.example.android.mooddiary.common.HomeActivity;
import com.example.android.mooddiary.diary.db.DiaryDatabaseHelper;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.utils.GetDate;
import com.example.android.mooddiary.diary.widget.LinedEditText;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.trity.floatingactionbutton.FloatingActionButton;
import cc.trity.floatingactionbutton.FloatingActionsMenu;

/**
 * 添加日记的 Activity
 * <p>
 * Created by developerHaoz on 2017/5/3.
 */

public class AddDiaryActivity extends AppCompatActivity {

    @Bind(R.id.add_diary_et_title)
    EditText mAddDiaryEtTitle;
    @Bind(R.id.add_diary_et_content)
    LinedEditText mAddDiaryEtContent;
    @Bind(R.id.add_diary_fab_save)
    FloatingActionButton mAddDiaryFabBack;
    @Bind(R.id.add_diary_fab_back)
    FloatingActionButton mAddDiaryFabAdd;
    @Bind(R.id.right_labels)
    FloatingActionsMenu mRightLabels;
    @Bind(R.id.home_iv_draw)
    ImageView mIvDraw;
    @Bind(R.id.home_tv_title_normal)
    TextView mTvTitle;
    @Bind(R.id.home_iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.contacts_tab_rl)
    LinearLayout mContactsTabRl;
    @Bind(R.id.mood_seekBar)
    SeekBar moodSeekBar;
    @Bind(R.id.item_iv_photo)
    ImageView itemIvPhoto;
    @Bind(R.id.add_diary_fab_photo)
    FloatingActionButton addDiaryFabPhoto;

    private DiaryDatabaseHelper mHelper;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        initToolbar();
        initView(intent);
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);

    }

    private void initToolbar() {
        mIvDraw.setImageResource(R.drawable.app_back);
        mTvTitle.setText("添加日记");
        mIvMenu.setVisibility(View.GONE);
    }

    private void initView(Intent intent) {
        mAddDiaryEtTitle.setText(intent.getStringExtra("title"));
        mAddDiaryEtContent.setText(intent.getStringExtra("content"));
    }


    @OnClick({R.id.home_iv_draw, R.id.add_diary_fab_save, R.id.add_diary_fab_back,R.id.add_diary_fab_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_iv_draw:
                backToDiaryFragment();
            case R.id.add_diary_fab_save:
                String date = GetDate.getDate().toString();
                String tag = String.valueOf(System.currentTimeMillis());
                String title = mAddDiaryEtTitle.getText().toString() + "";
                String content = mAddDiaryEtContent.getText().toString() + "";
                int mood = moodSeekBar.getProgress();

                if (!title.equals("") || !content.equals("")) {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("date", date);
                    values.put("title", title);
                    values.put("content", content);
                    values.put("tag", tag);
                    values.put("mood", mood);
                    db.insert("Diary", null, values);
                    values.clear();
                }
                finish();
                break;
            case R.id.add_diary_fab_back:
                backToDiaryFragment();
                break;
            case R.id.add_diary_fab_photo:
                    takePhoto();
                break;
        }
    }
    private void takePhoto(){

    }
    private void backToDiaryFragment() {//返回
        final String dateBack = GetDate.getDate().toString();
        final String titleBack = mAddDiaryEtTitle.getText().toString();
        final String contentBack = mAddDiaryEtContent.getText().toString();
        if (!titleBack.isEmpty() || !contentBack.isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("是否保存日记内容？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("date", dateBack);
                    values.put("title", titleBack);
                    values.put("content", contentBack);
                    db.insert("Diary", null, values);
                    values.clear();
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.home_iv_draw)
    public void onViewClicked() {
        finish();
    }
}
