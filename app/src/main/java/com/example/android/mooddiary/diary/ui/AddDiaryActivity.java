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
import com.example.android.mooddiary.diary.db.DiaryDatabaseHelper;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.utils.GetDate;
import com.example.android.mooddiary.diary.utils.PictureUtils;
import com.example.android.mooddiary.diary.widget.LinedEditText;

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.trity.floatingactionbutton.FloatingActionButton;
import cc.trity.floatingactionbutton.FloatingActionsMenu;


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
    private File mPhotoFile;//照片
    private Diary mDiary = new Diary();//日记类
    private static final int REQUEST_PHOTO = 0;

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

        UUID mUUID = UUID.randomUUID();
        String id = mUUID.toString();
        mDiary.setId(id);
        takePhoto();

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
        mTvTitle.setText("添加日记");
        mIvMenu.setVisibility(View.GONE);
    }

    private void initView(Intent intent) {
        updatePhotoView();
        mAddDiaryEtTitle.setText(intent.getStringExtra("title"));
        mAddDiaryEtContent.setText(intent.getStringExtra("content"));
    }

    private void takePhoto() {//拍照功能
        mPhotoFile = mDiary.getPhotoFile();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mPhotoFile != null);
        addDiaryFabPhoto.setEnabled(canTakePhoto);

        addDiaryFabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(AddDiaryActivity.this, "com.example.android.mooddiary.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureImage, REQUEST_PHOTO);

                List<ResolveInfo> cameraActivities = AddDiaryActivity.this
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    AddDiaryActivity.this.grantUriPermission(activity.activityInfo.packageName,
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
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(AddDiaryActivity.this,
                    "com.example.android.mooddiary.fileprovider", mPhotoFile);
            AddDiaryActivity.this.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            itemIvPhoto.setImageResource(R.drawable.photo_size_select_actual_white_48dp);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), AddDiaryActivity.this);
            itemIvPhoto.setImageBitmap(bitmap);
        }
    }

    @OnClick({R.id.home_iv_draw, R.id.add_diary_fab_save, R.id.add_diary_fab_back,R.id.item_iv_photo})
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
                String id = mDiary.getId();

                if (!title.equals("") || !content.equals("")) {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("uuid", id);
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
            case R.id.item_iv_photo:
                Intent intent = new Intent(AddDiaryActivity.this, PictureActivity.class);
                String uuid = mDiary.getId();
                intent.putExtra("uuid",uuid);
                startActivity(intent);
                break;
        }
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
