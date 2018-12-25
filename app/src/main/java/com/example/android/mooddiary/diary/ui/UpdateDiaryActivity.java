package com.example.android.mooddiary.diary.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.example.android.mooddiary.diary.event.RefreshViewEvent;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.utils.GetDate;
import com.example.android.mooddiary.diary.utils.PictureUtils;
import com.example.android.mooddiary.diary.widget.LinedEditText;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.trity.floatingactionbutton.FloatingActionButton;
import cc.trity.floatingactionbutton.FloatingActionsMenu;

public class UpdateDiaryActivity extends AppCompatActivity {

    @Bind(R.id.update_diary_tv_date)
    TextView mUpdateDiaryTvDate;
    @Bind(R.id.update_diary_et_title)
    EditText mUpdateDiaryEtTitle;
    @Bind(R.id.update_diary_et_content)
    LinedEditText mUpdateDiaryEtContent;
    @Bind(R.id.update_diary_fab_del)
    FloatingActionButton mUpdateDiaryFabBack;
    @Bind(R.id.update_diary_fab_add)
    FloatingActionButton mUpdateDiaryFabAdd;
    @Bind(R.id.update_diary_fab_back)
    FloatingActionButton mUpdateDiaryFabDelete;
    @Bind(R.id.right_labels)
    FloatingActionsMenu mRightLabels;
    @Bind(R.id.update_diary_tv_tag)
    TextView mTvTag;
    @Bind(R.id.home_iv_draw)
    ImageView mIvDraw;
    @Bind(R.id.home_tv_title_normal)
    TextView mTvTitle;
    @Bind(R.id.home_tv_title_center)
    TextView mTvCenter;
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
    private File mPhotoFile;//照片
    private static final int REQUEST_PHOTO = 0;

    public static void startActivity(Context context, String id,String title, String content, String tag, int mood) {
        Intent intent = new Intent(context, UpdateDiaryActivity.class);
        intent.putExtra("uuid",id);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("tag", tag);
        intent.putExtra("mood", mood);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_diary);
        ButterKnife.bind(this);
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        Intent intent = getIntent();
        initToolbar();
        initView(intent);
        takePhoto(intent);
        Logger.d(mIvDraw.getContext() + " Activity");

        seekbarlisten();

    }
    //滑动监听
    private void seekbarlisten(){
        moodSeekBar.getProgressDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = SmileId(progress);
                Drawable drawable = getResources().getDrawable(id);//新的图片转成drawable对象
                moodSeekBar.setThumb(drawable);//设置新的图片

            }
        });
    }
    //心情变化图片
    public static int SmileId(int processed){
        int id;
        double flag = processed*1.0/100;
        if(flag<0.1){
            id=R.drawable.round_sentiment_very_dissatisfied_black_18;
        }else if(flag>=0.1&&flag<0.2){
            id=R.drawable.round_sentiment_dissatisfied_black_18;
        }else if(flag>=0.2&&flag<0.4){
            id=R.drawable.round_mood_bad_black_18;
        }else if(flag>=0.4&&flag<0.6){
            id=R.drawable.round_sentiment_satisfied_black_18;
        }else if(flag>=0.6&&flag<0.8){
            id=R.drawable.round_mood_black_18;
        }else{
            id=R.drawable.round_sentiment_very_satisfied_black_18;
        }
        return id;
    }
    @Override
    public void onResume() {
        super.onResume();
        updatePhotoView();
    }

    private void initToolbar() {
        mIvDraw.setImageResource(R.drawable.app_back);
        mIvDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvTitle.setText("修改日记");
        mIvMenu.setVisibility(View.GONE);
    }

    private void initView(Intent intent) {
        mUpdateDiaryTvDate.setText("" + GetDate.getDate());
        mUpdateDiaryEtTitle.setText(intent.getStringExtra("title"));
        mUpdateDiaryEtContent.setText(intent.getStringExtra("content"));
        mTvTag.setText(intent.getStringExtra("tag"));
        moodSeekBar.setProgress(intent.getIntExtra("mood", 0));
        updatePhotoView();
    }

    private void takePhoto(Intent intent) {//拍照功能
        Diary diary = new Diary();
        diary.setId(intent.getStringExtra("uuid"));
        mPhotoFile = diary.getPhotoFile();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mPhotoFile != null);
        addDiaryFabPhoto.setEnabled(canTakePhoto);

        addDiaryFabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(UpdateDiaryActivity.this, "com.example.android.mooddiary.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureImage, REQUEST_PHOTO);

                List<ResolveInfo> cameraActivities = UpdateDiaryActivity.this
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    UpdateDiaryActivity.this.grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(UpdateDiaryActivity.this,
                    "com.example.android.mooddiary.fileprovider",mPhotoFile);
            UpdateDiaryActivity.this.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            itemIvPhoto.setImageResource(R.drawable.photo_size_select_actual_white_48dp);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), UpdateDiaryActivity.this);
            itemIvPhoto.setImageBitmap(bitmap);
        }
    }

    @OnClick({R.id.home_iv_draw, R.id.update_diary_fab_del, R.id.update_diary_fab_add, R.id.update_diary_fab_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_iv_draw:
                finish();
            case R.id.update_diary_fab_del:
                showTips();
                break;
            case R.id.update_diary_fab_add:
                SQLiteDatabase dbUpdate = mHelper.getWritableDatabase();
                ContentValues valuesUpdate = new ContentValues();

                String title = mUpdateDiaryEtTitle.getText().toString();
                String content = mUpdateDiaryEtContent.getText().toString();
                int mood = moodSeekBar.getProgress();

                valuesUpdate.put("title", title);
                valuesUpdate.put("content", content);
                valuesUpdate.put("mood", mood);
                dbUpdate.update("Diary", valuesUpdate, "title = ?", new String[]{title});
                dbUpdate.update("Diary", valuesUpdate, "content = ?", new String[]{content});
                dbUpdate.update("Diary", valuesUpdate, "mood = ?", new String[]{String.valueOf(mood)});
                finish();
                break;
            case R.id.update_diary_fab_back:
                finish();
                break;
        }
    }

    private void showTips() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("确定要删除该日记吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String tag = mTvTag.getText().toString();
                SQLiteDatabase dbDelete = mHelper.getWritableDatabase();
                dbDelete.delete("Diary", "tag = ?", new String[]{tag});
                finish();
                HomeActivity.startActivity(UpdateDiaryActivity.this);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
        EventBus.getDefault().post(new RefreshViewEvent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
