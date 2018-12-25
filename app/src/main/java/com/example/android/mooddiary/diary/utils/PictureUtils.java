package com.example.android.mooddiary.diary.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path,int destWidth,int destHeight){
        //读取图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //具体大小的确定
        int inSampleSize = 1;
        if(srcHeight >destHeight||srcWidth>destWidth){
            float heightScale = srcHeight/destHeight;
            float widthScale = srcWidth/destWidth;

            inSampleSize = Math.round(heightScale>widthScale?heightScale:widthScale);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //重新读取，创建bitmap对象
        return BitmapFactory.decodeFile(path,options);
    }
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path,size.y,size.x);
    }
}
