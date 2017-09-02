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

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class HJTest8 extends  HJTest7 {

    //退出流程
    @Override
    public void quit3() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        //UiObject my_page = mUIDevice.findObject(new UiSelector().text("我的主页"));
        //my_page.click();

        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipe_target"));
        home.flingForward();
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject setting = mUIDevice.findObject(new UiSelector().text("设置"));
        setting.click();
        Thread.sleep(2000);
        //关闭账号保护
        UiObject anquan = mUIDevice.findObject(new UiSelector().text("账号安全"));
        anquan.click();
        Thread.sleep(2000);
        UiObject baohu = mUIDevice.findObject(new UiSelector().text("账号保护"));
        baohu.click();
        Thread.sleep(1000);
        //开关
        UiObject kaiguan = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/toggle_account_protection"));
        kaiguan.click();
        Thread.sleep(500);
        //提示框
        UiObject2 tishi = mUIDevice.findObject(By.text("确定"));
        if(tishi==null){
            kaiguan.click();
            Thread.sleep(500);
            tishi = mUIDevice.findObject(By.text("确定"));
            if(tishi!=null){
                tishi.click();
            }
        }else{
            tishi.click();
        }
        Thread.sleep(1000);
        mUIDevice.pressBack();
        mUIDevice.pressBack();

        UiObject2 set = mUIDevice.findObject(By.res("android:id/content")).getChildren().get(1).getChildren().get(1);
        set.scroll(Direction.DOWN, 0.8f);
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();  //点击按键
        //Thread.sleep(1000);
    }

}