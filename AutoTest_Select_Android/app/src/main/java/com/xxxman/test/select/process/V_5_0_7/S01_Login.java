package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.ToastUitl;

/**
 * 登录
 * Created by tuzi on 2017/10/22.
 */

public class S01_Login {

    private static final String TAG = S01_Login.class.getName();

    public static void start(Task task) throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();;
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
        if(login==null){
            S01_Quit.start(task,false);
            my = mUIDevice.findObject(new UiSelector().text("我的"));
            my.click();
            UiObject2 login1 = mUIDevice.findObject(By.text("使用手机号登录"));
            login1.click();
        }else {
            login.click();
        }
        UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入您的手机号"));
        phone.setText(task.getPhone());
        UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
        password.setText(task.getPwd());
        UiObject logining = mUIDevice.findObject(new UiSelector().text("登录"));
        logining.click();
        ToastUitl.sendBroadcast(mContext,"正在登录第"+task.getNumber()+"个用户");
    }
}
