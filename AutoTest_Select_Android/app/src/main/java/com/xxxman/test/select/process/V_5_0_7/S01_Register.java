package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;

import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.SQLUtil;
import com.xxxman.test.select.util.ToastUitl;

/**
 * 登录
 * Created by tuzi on 2017/10/22.
 */

public class S01_Register {

    private static final String TAG = S01_Register.class.getName();

    public static void start() throws Exception{
        String s_phone = "1234";
        String s_code  ="1234";
        String s_pwd  = "qaz147258";
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();;
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
        if(login==null){
            S01_Quit.start(new Task(),false);
            my = mUIDevice.findObject(new UiSelector().text("我的"));
            my.click();
            UiObject2 login1 = mUIDevice.findObject(By.text("使用手机号登录"));
            login1.click();
        }else {
            login.click();
        }
        UiObject reg = mUIDevice.findObject(new UiSelector().text("注册"));
        reg.click();
        Thread.sleep(5000);
        /*****---------------------------*****/
        UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入手机号"));
        phone.setText(s_phone);
        UiObject get_code = mUIDevice.findObject(new UiSelector().text("获取短信验证码"));
        get_code.click();
        Thread.sleep(5000);
        /*****---------------------------*****/
        UiObject code = mUIDevice.findObject(new UiSelector().text("请输入验证码"));
        code.setText(s_code);
        UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
        password.setText(s_pwd);
        UiObject reg2 = mUIDevice.findObject(new UiSelector().text("注册"));
        reg2.click();
        UiObject2 suc = mUIDevice.findObject(By.text("成功"));
        if(suc!=null){
            FileUtil.write("register_"+ SQLUtil.getDayString()+".txt",s_phone+","+s_pwd);
        }
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
    }
}
