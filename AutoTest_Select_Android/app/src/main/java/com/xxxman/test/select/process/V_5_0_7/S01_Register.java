package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.SQLUtil;
import com.xxxman.test.select.util.ToastUitl;
import com.xxxman.test.select.util.XingjkAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 * Created by tuzi on 2017/10/22.
 */

public class S01_Register {

    private static final String TAG = S01_Register.class.getName();

    public static boolean start(String reg_pwd,String project_id,String token) throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();
        String s_phone = null;
        String s_code  = null;
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
        s_phone = XingjkAPI.getPhone(project_id,token);
        if(s_phone!=null){
            /*****---------------------------*****/
            UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入手机号"));
            phone.setText(s_phone);
            UiObject get_code = mUIDevice.findObject(new UiSelector().text("获取短信验证码"));
            get_code.click();
            Thread.sleep(5000);
            s_code = XingjkAPI.getMessage(project_id,s_phone,token);
            if(s_code==null){
                ToastUitl.sendBroadcast(mContext,"等待下次获取验证码");
                Thread.sleep(5000);
                s_code = XingjkAPI.getMessage(project_id,s_phone,token);
            }
            if(s_code==null){
                ToastUitl.sendBroadcast(mContext,"等待下次获取验证码");
                Thread.sleep(5000);
                s_code = XingjkAPI.getMessage(project_id,s_phone,token);
            }
            if(s_code==null){
                ToastUitl.sendBroadcast(mContext,"等待下次获取验证码");
                Thread.sleep(5000);
                s_code = XingjkAPI.getMessage(project_id,s_phone,token);
            }
            if(s_code!=null){
                UiObject code = mUIDevice.findObject(new UiSelector().text("请输入验证码"));
                code.setText(s_code);
                UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
                password.setText(reg_pwd);
                UiObject reg2 = mUIDevice.findObject(new UiSelector().text("注册"));
                reg2.click();
                Thread.sleep(2000);
                UiObject2 register_do = mUIDevice.findObject(By.text("立即登录"));
                if(register_do!=null){
                    Log.d(TAG,"注册失败，已注册过="+s_phone+","+reg_pwd);
                    FileUtil.write("花椒注册失败_"+ SQLUtil.getDayString()+".txt",s_phone+","+"已注册过");
                    return false;
                }
                UiObject my1 = mUIDevice.findObject(new UiSelector().text("我的"));
                my1.click();
                Log.d(TAG,"注册成功="+s_phone+","+reg_pwd);
                FileUtil.write("花椒注册_"+ SQLUtil.getDayString()+".txt",s_phone+","+reg_pwd);
                return true;
            }else{
                Log.d(TAG,"没获得验证码="+s_phone+","+reg_pwd);
                ToastUitl.sendBroadcast(mContext,"注册失败，手机"+s_phone+"没有获取验证码");
                FileUtil.write("花椒注册失败_"+ SQLUtil.getDayString()+".txt",s_phone+","+"没获得验证码");
                return false;
            }
        }
        return false;
    }
}
