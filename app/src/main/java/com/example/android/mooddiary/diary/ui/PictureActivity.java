package com.example.android.mooddiary.diary.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.mooddiary.R;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.utils.PictureUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class PictureActivity extends AppCompatActivity {

    @Bind(R.id.detail_pv_show_photo)
    PhotoView detailPvShowPhoto;

    private File mPhotoFile;//照片
    private Diary mDiary= new Diary();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String id = intent.getStringExtra("uuid");
        mDiary.setId(id);
        mPhotoFile = mDiary.getPhotoFile();
        updatePhotoView();
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            detailPvShowPhoto.setImageResource(R.drawable.photo_size_select_actual_white_48dp);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), PictureActivity.this);
            detailPvShowPhoto.setImageBitmap(bitmap);
        }
    }
}
