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
public class HJTest2 extends  HJTest1{

    @Test
    public void test_for(){
        List<User> list = sqlUtil.selectLoginCount();
        try {
            if(list.size()>0){
                test2(list.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void test2(User user) throws Exception {

        boot();             //0.启动
        //closeAd();          //尝试关广告
        login3(user);            //1.登录
        //closeAd();         //尝试关广告
    }
}

