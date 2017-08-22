package com.xxxman.autotest.shell;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TabLayout mTablayout;
    ViewPager mViewPager;
    TabTitlePager mTabTitleAdapter;
    String[] titleArr = new String[]{"阳光","红包","礼物"};
    private static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTablayout = (TabLayout) findViewById(R.id.tabLayout);
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.setTabMode(TabLayout.MODE_FIXED);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabTitleAdapter = new TabTitlePager(getSupportFragmentManager());
        mViewPager.setAdapter(mTabTitleAdapter);
        mTablayout.setupWithViewPager(mViewPager);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_menu:
                Log.d(TAG,"程序注册");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            case R.id.udate_menu:
                Log.d(TAG,"下载更新");
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, UpdateActivity.class);
                MainActivity.this.startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class TabTitlePager extends FragmentPagerAdapter {

        public TabTitlePager(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = SunFragment.newInstance();
            if(position ==0 ){
                fragment = SunFragment.newInstance();
            }
            if(position ==1 ){
                fragment = HongbaoFragment.newInstance();
            }
            if(position ==2 ){
                fragment = GiftFragment.newInstance();
            }
            return fragment;
        }
        @Override
        public int getCount() {
            return titleArr.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titleArr[position];
        }
    }
}
