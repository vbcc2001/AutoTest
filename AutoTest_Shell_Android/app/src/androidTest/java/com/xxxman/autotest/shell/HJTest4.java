package com.xxxman.autotest.shell;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class HJTest4 extends HJTest3{

    String TAG = "HJTest4";
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("notifation", new UiWatcher() {

            @Override
            public boolean checkForCondition() {
                Log.d(TAG,":进入Watcher");
                try {
                    closeAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
    @Test
    public void test(){

        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);


        List<User> list = sqlUtil.selectLoginCount();
        try {
            if(list.size()>0) {
                User user = list.get(0);
                login(user);
                for (int i = 0; i < 6; i++) {
                    try {
                        if (i==0 || i==2 || i==4){
                            find_money(user,"最新");
                        }
                        if (i==1 || i==3 || i==5){
                            UiObject2 city = mUIDevice.findObject(By.text("深圳"));
                            if (city!=null){
                                find_money(user,"深圳");
                            }
                            city = mUIDevice.findObject(By.text("北京"));
                            if (city!=null){
                                find_money(user,"北京");
                            }
                            city = mUIDevice.findObject(By.text("上海"));
                            if (city!=null){
                                find_money(user,"上海");
                            }
                            city = mUIDevice.findObject(By.text("广州"));
                            if (city!=null){
                                find_money(user,"广州");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reboot();
                    }
                    if (count_get_hongbao>=6){
                        break;
                    }
                }
                quit(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
        }
    }
}
