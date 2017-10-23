package com.xxxman.test.select;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xxxman.test.select.fragment.GiftFragment;
import com.xxxman.test.select.fragment.HongbaoFragment;
import com.xxxman.test.select.fragment.SunFragment;
import com.xxxman.test.select.menu.LoginActivity;
import com.xxxman.test.select.menu.MoneyActivity;
import com.xxxman.test.select.menu.UpdateActivity;

public class MainActivity extends AppCompatActivity {

    String[] titleArr = new String[]{"红包","阳光","礼物"};
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabTitlePager mTabTitleAdapter = new TabTitlePager(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mTabTitleAdapter);

        TabLayout mTablayout = (TabLayout) findViewById(R.id.tabLayout);
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
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
        Intent intent = new Intent();
        Log.d(TAG,"点击目录为："+item.getItemId());
        Log.d(TAG,"点击目录为："+R.id.login_menu);
        Log.d(TAG,"点击目录为："+R.id.udate_menu);
        Log.d(TAG,"点击目录为："+R.id.money_menu);
        switch (item.getItemId()) {
            case R.id.login_menu:
                intent.setClass(MainActivity.this, LoginActivity.class);
                break;
            case R.id.udate_menu:
                intent.setClass(MainActivity.this, UpdateActivity.class);
                break;
            case R.id.money_menu:
                intent.setClass(MainActivity.this, MoneyActivity.class);
                break;
            default:
                intent.setClass(MainActivity.this, LoginActivity.class);
        }
        MainActivity.this.startActivity(intent);
        return true;
    }
    private class TabTitlePager extends FragmentPagerAdapter {

        public TabTitlePager(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment ;
            if(position ==0 ){
                fragment = HongbaoFragment.newInstance();
            }else if(position ==1 ){
                fragment = SunFragment.newInstance();
            }else if(position ==2 ){
                fragment = GiftFragment.newInstance();
            }else{
                fragment = HongbaoFragment.newInstance();
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
