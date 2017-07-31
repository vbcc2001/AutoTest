package com.xxxman.autotest.shell;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;
import android.view.KeyEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class HJTest1 {

    private UiDevice mUIDevice = null;
    private Context mContext = null;
    String TAG = "HJTest1";
    String APP = "com.huajiao";
    int log_count = 0;
    int count_get_sun = 0;
    SQLUtil sqlUtil = new SQLUtil();
    boolean is4X=false;
//    boolean is4X=true;
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("notifation", new UiWatcher() {

            @Override
            public boolean checkForCondition() {
                // just press back
                Log.d(TAG,":进入Watcher");
                try {
                    closeAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //mUIDevice.pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
                return false;
            }
        });
        //mUIDevice.waitForIdle(3000); //设置等待时间
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
        //mUIDevice.pressHome();  //按home键
    }
    //@Test
    public void test_test() {
        List<User> list = sqlUtil.selectFailCount();
        try {
            getSunshine(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void test_for(){

        List<User> list = sqlUtil.selectUser();
        for(User user:list) {
            Log.d(TAG,user.pwd+"---");
            try {
                //执行任务
                sqlUtil.updateTaskCount(user);
                test1(user) ;
                //完成任务
                sqlUtil.updateEndCount(user);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    reboot();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        list = sqlUtil.selectFailCount();
        for(User user:list) {
            Log.d(TAG,user.pwd+"---");
            try {
                //执行任务
                sqlUtil.updateTaskCount(user);
                test1(user) ;
                //完成任务
                sqlUtil.updateEndCount(user);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    reboot();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath();
            list = sqlUtil.selectFailCount();
            FileUtil.writeTxtFile(list,path,"fail_"+sqlUtil.dateString+".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test1(User user) throws Exception {

        int count_share = 0;

        //boot();             //0.启动
        //closeAd();          //尝试关广告
        login(user);            //1.登录
        //closeAd();         //尝试关广告
        goGuangChang();         //3.进入广场
        goZhiBo();        //4.进入直播
        //5.分享3次
        for(int i = 0 ; i < 3 ; i++) {
            share();
            count_share++;
        }
        getSunshine(user);          //6.领取阳光
        closeZhiBo();       //7.关直播
        //closeAd();          //尝试关广告
        quit();         //8.退出
    }
    //启动流程
    public void boot() throws Exception {
        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
    }
    public void reboot() throws Exception {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(APP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);

    }
    //登录流程
    public void login(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
        if(login==null){
            quit();
            my = mUIDevice.findObject(new UiSelector().text("我的"));
            my.click();
            UiObject2 login1 = mUIDevice.findObject(By.text("使用手机号登录"));
            login1.click();
        }else {
            login.click();
        }
        UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入您的手机号"));
        phone.setText(user.phone);
        //UiObject password = mUIDevice.findObject(new UiSelector().text("请输入密码"));
        UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
        password.setText(user.pwd);
        UiObject logining = mUIDevice.findObject(new UiSelector().text("登录"));
        logining.click();
        //Thread.sleep(2000);
    }
    //退出流程
    public void quit() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        //UiObject my_page = mUIDevice.findObject(new UiSelector().text("我的主页"));
        //my_page.click();

        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.flingForward();
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject setting = mUIDevice.findObject(new UiSelector().text("设置"));
        setting.click();
        UiObject2 set = mUIDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(1);
        set.scroll(Direction.DOWN, 0.8f);
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();  //点击按键
        //Thread.sleep(1000);
    }
    //关闭弹窗广告
    public void closeAd() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject2 close = mUIDevice.findObject(By.res("com.huajiao:id/img_close"));
        if(close!=null){
            Log.d(TAG,"有广告");
            close.click();
        }
    }
    //进入广场
    public void goGuangChang() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject guangchang = mUIDevice.findObject(new UiSelector().text("广场"));
        guangchang.click();
    }
    //进入直播
    public void goZhiBo() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/icon_view"));
        zhibozhong.click();
    }
    //退出直播
    public void closeZhiBo() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject close = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_live_close"));
        //close.click();  //点击按键
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(990,1842);
        }else{
            mUIDevice.click(660,1228);
        }
    }
    //分享
    public void share() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        Thread.sleep(1000);
        mUIDevice.click(2,2);
        Thread.sleep(1000);
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
    public void getSunshine(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject sun =mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/sun_task_tip"));
        //sun.click();
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(976,390);
        }else{
            mUIDevice.click(651,268);
        }
        UiObject2 get = mUIDevice.findObject(By.text("分享直播(3/3)"));
        if(get!=null){
            UiObject2 get_ = get.getParent().findObject(By.text("领取"));
            get_.click();
            sqlUtil.updateSuccessCount(user);
            Log.i(TAG,"count_get_sun:"+count_get_sun);
        }else{
            UiObject2 list = mUIDevice.findObject(By.res("com.huajiao:id/list_view"));
            list.scroll(Direction.DOWN, 0.8f);
            UiObject2 get1 = mUIDevice.findObject(By.text("分享直播(3/3)"));
            UiObject2 end = get1.getParent().findObject(By.text("已完成"));
            if(end != null){
                sqlUtil.updateSuccessCount(user);
                Log.i(TAG,"count_get_sun:"+count_get_sun);
            }
        }
        mUIDevice.pressBack();
    }
}