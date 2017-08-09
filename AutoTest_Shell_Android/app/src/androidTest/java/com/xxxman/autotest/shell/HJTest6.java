package com.xxxman.autotest.shell;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.runner.RunWith;


/**
 * Created by tuzi on 2017/8/9.
 */

@RunWith(AndroidJUnit4.class)
public class HJTest6 extends  HJTest1{

    //分享
    public void recordVideo() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        Thread.sleep(1000);
        mUIDevice.click(2,2);
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(880,1842);
        }else{
            mUIDevice.click(560,1228);
        }
        Thread.sleep(200);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(560,1228);
        }
        Thread.sleep(7000);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(560,1228);
        }
        Thread.sleep(7000);
        UiObject share_qq = mUIDevice.findObject(new UiSelector().text("发给QQ好友"));
        share_qq.click();

        UiObject my_compa = mUIDevice.findObject(new UiSelector().text("我的电脑"));
        my_compa.click();  //点击按键

        UiObject qq_sent = mUIDevice.findObject(new UiSelector().text("发送"));
        qq_sent.click();  //点击按键

        UiObject qq_back = mUIDevice.findObject(new UiSelector().text("返回花椒直播"));
        qq_back.click();  //点击按键

    }
}

