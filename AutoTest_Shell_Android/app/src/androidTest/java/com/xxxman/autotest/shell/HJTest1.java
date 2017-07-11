package com.xxxman.autotest.shell;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class HJTest1 {

    private UiDevice mUIDevice = null;
    private Context mContext = null;
    String APP = "com.huajiao";
    int log_count = 0;
    int count_get_sun = 0;

    @Before
    public void setUp() throws RemoteException {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mContext = InstrumentationRegistry.getContext();

        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
        mUIDevice.pressHome();  //按home键
    }
    @Test
    public void test_for(){

        for(int i = 0 ; i <2 ; i++) {
            try {
                test1() ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                setUp();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
    //启动流程
    public void boot() throws Exception {
        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
    }
    //登录流程
    public void login() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        //先尝试关广告
        closeAd();
        if (!my.exists()){
            //重启一次
            boot();
            mUIDevice.pressBack();
            my = mUIDevice.findObject(new UiSelector().text("我的"));
        }
        my.click();
        UiObject login = mUIDevice.findObject(new UiSelector().text("使用手机号登录"));
        if (!login.exists()){
            //退出一次
            quit();
            my = mUIDevice.findObject(new UiSelector().text("我的"));
            if (!my.exists()){
                //重启一次
                boot();
                my = mUIDevice.findObject(new UiSelector().text("我的"));
            }
            my.click();
            login = mUIDevice.findObject(new UiSelector().text("使用手机号登录"));
        }
        login.click();
        UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入您的手机号"));
        phone.setText("18926085629");
        UiObject password = mUIDevice.findObject(new UiSelector().text("请输入密码"));
        password.setText("x789456*");
        UiObject logining = mUIDevice.findObject(new UiSelector().text("登录"));
        logining.click();
        Thread.sleep(2000);
    }
    //退出流程
    public void quit() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject my_page = mUIDevice.findObject(new UiSelector().text("我的主页"));
        my_page.click();
        
        mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject setting = mUIDevice.findObject(new UiSelector().text("设置"));
        setting.click();
        mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();  //点击按键
        Thread.sleep(1000);
    }
    //关闭弹窗广告
    public void closeAd() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject2 close = mUIDevice.findObject(By.res("com.huajiao:id/img_close"));
        if(close!=null){
            close.click();
        }
    }
    //进入广场
    public void goGuangChang() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject guangchang = mUIDevice.findObject(new UiSelector().text("广场"));
        guangchang.click();
//        UiObject remen = mUIDevice.findObject(new UiSelector().text("热门"));
//        remen.click();
    }
    //进入直播
    public void goZhiBo() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/icon_view"));
            zhibozhong.click();
    }
    //进入直播
    public void closeZhiBo() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject close = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_live_close"));
        if(close.exists()){
            close.click();  //点击按键
        }
    }
    //分享
    public void share() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        share.click();

        UiObject share_qq = mUIDevice.findObject(new UiSelector().text("发给QQ好友"));
        share_qq.click();

        UiObject my_compa = mUIDevice.findObject(new UiSelector().text("我的电脑"));
        my_compa.click();  //点击按键

        UiObject qq_sent = mUIDevice.findObject(new UiSelector().text("发送"));
        qq_sent.click();  //点击按键

        UiObject qq_back = mUIDevice.findObject(new UiSelector().text("返回花椒直播"));
        qq_back.click();  //点击按键

    }
    public void getSunshine() throws Exception {
        Log.d("--------",(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d("--------","上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject sun =mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/sun_task_tip"));
        if (!sun.exists()){
            //退出重进一次
            mUIDevice.pressBack();
            mUIDevice.pressBack();
            goGuangChang();
            goZhiBo();
            sun =mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/sun_task_tip"));
        }
        sun.click();
        UiObject2 get = mUIDevice.findObject(By.text("分享直播(3/3)"));
        if(get!=null){
            UiObject2 get_ = get.getParent().findObject(By.text("领取"));
            if(get_!=null){
                get_.click();
            }
            UiObject2 end = get.getParent().findObject(By.text("已完成"));
            if(end != null){
                count_get_sun ++;
                Log.i("-----","count_get_sun:"+count_get_sun);
            }
        }
        mUIDevice.pressBack();
    }
    public void test1() throws Exception {

        int count_share = 0;
        //0.启动
        boot();
        //尝试关广告
        closeAd();
        //1.登录
        login();
        //2.关广告
        closeAd();
        //3.进入广场
        goGuangChang();
        //4.进入直播
        goZhiBo();
        //5.分享3次
        for(int i = 0 ; i < 3 ; i++) {
            share();
            count_share++;
        }
        //6.领取阳光
        if(count_share == 3){
            getSunshine();
        }else{
            //重启一次
            setUp();
            //进入直播
            goZhiBo();
            getSunshine();
        }
        //7.关直播
        closeZhiBo();
        //8.退出
        quit();
    }
}
