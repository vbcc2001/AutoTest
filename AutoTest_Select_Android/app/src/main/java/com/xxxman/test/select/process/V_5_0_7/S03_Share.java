package com.xxxman.test.select.process.V_5_0_7;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;

import com.xxxman.test.select.Constant;

/**
 * 分享
 * Created by tuzi on 2017/10/22.
 */

public class S03_Share {

    private static final String TAG = S03_Share.class.getName();

    public static void start() throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        boolean is4X = Constant.IS_4X();
        if(is4X){
            mUIDevice.click(840,1842);
        }else{
            mUIDevice.click(560,1228);
        }
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
