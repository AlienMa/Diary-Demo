package com.example.android.mooddiary.common;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mooddiary.R;
import com.example.android.mooddiary.common.view.CommonPagerAdapter;
import com.example.android.mooddiary.common.view.CommonTabBean;
import com.example.android.mooddiary.diary.utils.Diary;
import com.example.android.mooddiary.diary.event.StartUpdateDiaryEvent;
import com.example.android.mooddiary.diary.ui.DiaryFragment;
import com.example.android.mooddiary.diary.ui.UpdateDiaryActivity;
import com.example.android.mooddiary.diary.utils.PermissionUtils;
import com.example.android.mooddiary.picture.ui.MeiziFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.home_view_pager)
    ViewPager mHomeVp;
    @Bind(R.id.home_tab_layout)
    CommonTabLayout mHomeTabLayout;
    @Bind(R.id.home_dl)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.home_iv_draw)
    ImageView mIvDraw;
    @Bind(R.id.home_tv_title_normal)
    TextView mTvNormal;
    @Bind(R.id.home_tv_title_center)
    TextView mTvCenter;
    @Bind(R.id.home_iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.contacts_tab_rl)
    LinearLayout mContactsTabRl;

    private static final int[] SELECTED_ICONS = new int[]{R.drawable.diary_selected, R.drawable.photo_selectd};//R.drawable.photo_camera,
    private static final int[] UN_SELECTED_ICONS = new int[]{R.drawable.diary_unselected,R.drawable.photo_unselectd};//R.drawable.photo_camera_un,
    private static final String[] TITLES = new String[]{"日记","风景"};//,"日拍"

    private List<Fragment> mFragments;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTabLayout();
        initViewPager();
        initToolbar();
        permission();
    }

    private void permission() {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            String[] permissionsNeedCheck = PermissionUtils.checkPermission(this, permissions);
            if(permissionsNeedCheck != null){
                PermissionUtils.grantPermission(this, permissionsNeedCheck, PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
            }
    }

    private void initToolbar() {
        mIvDraw.setVisibility(View.GONE);
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setImageResource(R.drawable.bar_chart_white);
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HelloChart.newIntent(HomeActivity.this);
                //intent.putExtra(EXTRA_MOOD, message);
                startActivity(intent);
            }
        });
        mTvCenter.setVisibility(View.VISIBLE);
        mTvNormal.setVisibility(View.GONE);
    }

    private void initViewPager() {
        mFragments = new ArrayList<>();
        mFragments.add(DiaryFragment.newInstance());
        mFragments.add(MeiziFragment.newInstance());
        CommonPagerAdapter adapter = new CommonPagerAdapter(getSupportFragmentManager(), mFragments);
        mHomeVp.setAdapter(adapter);
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabEntityList = new ArrayList<>();
        for (int i = 0; i < TITLES.length; i++) {
            tabEntityList.add(new CommonTabBean(TITLES[i]
                    , SELECTED_ICONS[i]
                    , UN_SELECTED_ICONS[i]));
        }

        mHomeTabLayout.setTabData(tabEntityList);
        mHomeTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mHomeVp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        mHomeVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mHomeTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mHomeVp.setOffscreenPageLimit(4);
        mHomeVp.setCurrentItem(1);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe
    public void startUpdateDiaryActivity(StartUpdateDiaryEvent event) {
        Diary diary = event.getDiary();
        String id=diary.getId();
        String title = diary.getTitle();
        String content = diary.getContent();
        String tag = diary.getTag();
        int mood=diary.getMood();
        UpdateDiaryActivity.startActivity(this,id, title, content, tag, mood);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: 在主页面按返回键时弹出对话框，提示用户是否退出程序
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode){
            case PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                if(PermissionUtils.isGrantedAllPermissions(permissions, grantResults)){
                    Toast.makeText(this, "你允许了全部授权", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "你拒绝了部分权限，可能造成程序运行不正常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}





























