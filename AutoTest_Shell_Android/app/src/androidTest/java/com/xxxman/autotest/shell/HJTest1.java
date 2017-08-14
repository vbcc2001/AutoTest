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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class HJTest1 {

    UiDevice mUIDevice = null;
    Context mContext = null;
    String TAG = "HJTest1";
    String APP = "com.huajiao";
    int log_count = 0;
    int count_get_sun = 0;
    SQLUtil sqlUtil = new SQLUtil();
    MyConnection my  = new MyConnection();
    String url = "http://vpn.m2ss.top:3000/action/lfs/action/FunctionAction";
    String phone= "";
    boolean is_colse_ad = true;
//    boolean is4X=false;
    boolean is4X=true;
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
    @Test
    public void test_test() {
        UiObject2 qiandao = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
        if(qiandao!=null){
            qiandao.click();
        }
        UiObject2 qiandao2 = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
        if(qiandao2!=null){
            qiandao2.click();
        }
    }
    @Test
    public void test_for() throws Exception {

        List<User> list = sqlUtil.selectUser();
        boot();
        for(User user:list) {
            Log.d(TAG,user.pwd+"---");
            try {
                //执行任务
                sqlUtil.updateTaskCount(user);
                //分享3次阳光
                //test1(user) ;
                //录像阳光
                //test2(user) ;
                //新版录像阳光
                test3(user) ;
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
                //分享3次阳光
                //test1(user) ;
                //录像阳光
                //test2(user) ;
                //新版录像阳光
                test3(user) ;
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
        //提醒
        Intent intent = new Intent();
        intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
        intent.putExtra("name", "当前正在登录第 "+user.number+" 个账户");
        mContext.sendBroadcast(intent);
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
        if(is_colse_ad){
            UiObject2 close = mUIDevice.findObject(By.res("com.huajiao:id/img_close"));
            if(close!=null){
                Log.d(TAG,"有广告");
                close.click();
            }
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
    public void getSunshine2(User user) throws Exception {
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
        UiObject2 get = mUIDevice.findObject(By.text("录制视频(1/1)"));
        if (get!=null){
            Thread.sleep(1500);
            get = mUIDevice.findObject(By.text("录制视频(1/1)"));
        }
        if(get!=null){
            UiObject2 get_ = get.getParent().findObject(By.text("领取"));
            get_.click();
            sqlUtil.updateSuccessCount(user);
            Log.i(TAG,"count_get_sun:"+count_get_sun);
        }else{
            UiObject2 list = mUIDevice.findObject(By.res("com.huajiao:id/list_view"));
            list.scroll(Direction.DOWN, 0.8f);
            UiObject2 get1 = mUIDevice.findObject(By.text("录制视频(1/1)"));
            UiObject2 end = get1.getParent().findObject(By.text("已完成"));
            if(end != null){
                sqlUtil.updateSuccessCount(user);
                Log.i(TAG,"count_get_sun:"+count_get_sun);
            }
        }
        //记录阳光数
        UiObject2 sun_count = mUIDevice.findObject(By.res("com.huajiao:id/right_tv"));
        if (sun_count != null){
            Log.d(TAG,"---当前阳光数为--"+sun_count.getText()+"---------");
            user.sun = Integer.valueOf(sun_count.getText());
        }
        //完成任务
        String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"sun\\\":"+user.sun+"}\"";
        String context = "{\"function\":\"F100004\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";
        Map<String,String> parms = new HashMap<>();
        parms.put("jsonContent",context);
        String rs = my.getContextByHttp(url,parms);
        Log.d(TAG,"http请求结果"+rs);

        mUIDevice.pressBack();
    }
    //分享
    public void recordVideo() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        //UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        if(is4X){
            Thread.sleep(1000);
        }else{
            Thread.sleep(3000);
        }
        mUIDevice.click(2,2);
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(680,1842);
        }else{
            mUIDevice.click(460,1228);
        }
        if(is4X){
            Thread.sleep(200);
        }else{
            Thread.sleep(1000);
        }
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(8000);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(5000);
        try{
            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
            send.click();
            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
            end.click();
        }catch (Exception e){
            Thread.sleep(5000);
            try{
                UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                send.click();
                UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                end.click();
            }catch (Exception e1){
                Thread.sleep(5000);
                try{
                    UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                    send.click();
                    UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                    end.click();
                }catch (Exception e2){
                    Thread.sleep(5000);
                    try{
                        UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                        send.click();
                        UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                        end.click();
                    }catch (Exception e3){
                        Thread.sleep(5000);
                        try{
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                            end.click();
                        }catch (Exception e4){
                            Thread.sleep(5000);
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                            end.click();
                        }
                    }
                }
            }
        }
    }
    //进入直播
    public void goZhiBo2() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject new_list = mUIDevice.findObject(new UiSelector().text("频道"));
        new_list.click();
        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/type_icon"));
        zhibozhong.click();
    }
    public void test2(User user) throws Exception {

        phone = sqlUtil.selectCode();
        if (phone.length()==13){
            phone = phone.substring(1);
        }
        login(user);            //1.登录
        goGuangChang();         //3.进入广场
        goZhiBo2();        //4.进入直播
        recordVideo();
        getSunshine2(user);          //6.领取阳光
        closeZhiBo();       //7.关直播
        quit();         //8.退出
    }
    public void test3(User user) throws Exception {

        phone = sqlUtil.selectCode();
        if (phone.length()==13){
            phone = phone.substring(1);
        }

        login3(user);            //1.登录
        for(int i=0; i<3;i++){
            UiObject2 qiandao = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
            if(qiandao!=null){
                qiandao.click();
            }
            Thread.sleep(1000);
            UiObject2 qiandao2 = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
            if(qiandao2!=null){
                qiandao2.click();
            }
        }
        goGuangChang3();         //3.进入广场
        goZhiBo3();        //4.进入直播
        recordVideo3();
        Thread.sleep(1000);
        getSunshine3(user);          //6.领取阳光
        closeZhiBo();       //7.关直播
        quit3();         //8.退出
    }
    //登录流程
    public void login3(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 login = mUIDevice.findObject(By.text("手机号登录"));
        if(login==null){
            quit3();
            my = mUIDevice.findObject(new UiSelector().text("我的"));
            my.click();
            UiObject2 login1 = mUIDevice.findObject(By.text("手机号登录"));
            login1.click();
        }else {
            login.click();
        }
        //提醒
        Intent intent = new Intent();
        intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
        intent.putExtra("name", "当前正在登录第 "+user.number+" 个账户");
        mContext.sendBroadcast(intent);
        UiObject phone = mUIDevice.findObject(new UiSelector().text("请输入手机号"));
        phone.setText(user.phone);
        //UiObject password = mUIDevice.findObject(new UiSelector().text("请输入密码"));
        UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
        password.setText(user.pwd);
        UiObject logining = mUIDevice.findObject(new UiSelector().text("登录"));
        logining.click();
    }
    //退出流程
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
        UiObject2 set = mUIDevice.findObject(By.res("android:id/content")).getChildren().get(1).getChildren().get(1);
        set.scroll(Direction.DOWN, 0.8f);
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();  //点击按键
        //Thread.sleep(1000);
    }
    //进入直播
    public void goZhiBo3() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject new_list = mUIDevice.findObject(new UiSelector().text("热门"));
        new_list.click();
        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/hot_new_grid_icon_view"));
        zhibozhong.click();
    }
    //分享
    public void recordVideo3() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        //UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        if(is4X){
            Thread.sleep(1000);
        }else{
            Thread.sleep(3000);
        }
        mUIDevice.click(2,2);
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(680,1842);
        }else{
            mUIDevice.click(460,1228);
        }
        if(is4X){
            Thread.sleep(200);
        }else{
            Thread.sleep(1000);
        }
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(8000);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(5000);
        //关闭广告监控
        is_colse_ad = false;
        try{
            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
            send.click();
            shareQQ();
        }catch (Exception e){
            Thread.sleep(5000);
            try{
                UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                send.click();
                shareQQ();
            }catch (Exception e1){
                Thread.sleep(5000);
                try{
                    UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                    send.click();
                    shareQQ();
                }catch (Exception e2){
                    Thread.sleep(5000);
                    try{
                        UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                        send.click();
                        shareQQ();
                    }catch (Exception e3){
                        Thread.sleep(5000);
                        try{
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            shareQQ();
                        }catch (Exception e4){
                            Thread.sleep(5000);
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            shareQQ();
                        }
                    }
                }
            }
        }finally {
            is_colse_ad = true;
        }
    }
    public void shareQQ() throws Exception {
        UiObject my_compa = mUIDevice.findObject(new UiSelector().text("我的电脑"));
        my_compa.click();  //点击按键

        UiObject qq_sent = mUIDevice.findObject(new UiSelector().text("发送"));
        qq_sent.click();  //点击按键

        UiObject qq_back = mUIDevice.findObject(new UiSelector().text("返回花椒直播"));
        qq_back.click();  //点击按键
    }
    public void getSunshine3(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject sun =mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/sun_task_tip"));
        //sun.click();
        Thread.sleep(2000);
        if(is4X){
            mUIDevice.click(976,390);
        }else{
            mUIDevice.click(651,268);
        }
        UiObject2 get = mUIDevice.findObject(By.text("录制并分享小视频(1/1)"));
        if (get!=null){
            Thread.sleep(1500);
            get = mUIDevice.findObject(By.text("录制并分享小视频(1/1)"));
        }
        if(get!=null){
            UiObject2 get_ = get.getParent().findObject(By.text("领取"));
            if(get_!=null){
                get_.click();
            }
            sqlUtil.updateSuccessCount(user);
            Log.i(TAG,"count_get_sun:"+count_get_sun);
        }else{
            UiObject2 list = mUIDevice.findObject(By.res("com.huajiao:id/list_view"));
            list.scroll(Direction.DOWN, 0.8f);
            UiObject2 get1 = mUIDevice.findObject(By.text("录制并分享小视频(1/1)"));
            UiObject2 end = get1.getParent().findObject(By.text("已完成"));
            if(end != null){
                sqlUtil.updateSuccessCount(user);
                Log.i(TAG,"count_get_sun:"+count_get_sun);
            }
        }
        //记录阳光数
        UiObject2 sun_count = mUIDevice.findObject(By.res("com.huajiao:id/right_tv"));
        if (sun_count != null){
            Log.d(TAG,"---当前阳光数为--"+sun_count.getText()+"---------");
            user.sun = Integer.valueOf(sun_count.getText());
        }
        //完成任务
        String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"sun\\\":"+user.sun+"}\"";
        String context = "{\"function\":\"F100004\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";
        Map<String,String> parms = new HashMap<>();
        parms.put("jsonContent",context);
        String rs = my.getContextByHttp(url,parms);
        Log.d(TAG,"http请求结果"+rs);
        mUIDevice.pressBack();
    }
    //关闭弹窗广告
    public void closeQiandao() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject2 close = mUIDevice.findObject(By.text("每日签到"));
        if(close!=null){
            Log.d(TAG,"有签到");
            //close.click();
            mUIDevice.pressBack();
        }
//        UiObject2 finsh = mUIDevice.findObject(By.text("完成"));
//        if(finsh!=null){
//            Log.d(TAG,"有签到");
//            finsh.click();
//        }
    }
    //进入广场
    public void goGuangChang3() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        try{
            UiObject guangchang = mUIDevice.findObject(new UiSelector().text("广场"));
            guangchang.click();
        }catch(Exception e) {
            reboot();
            UiObject guangchang = mUIDevice.findObject(new UiSelector().text("广场"));
            guangchang.click();
        }

//        UiObject2 guangchang = mUIDevice.findObject(By.text("广场"));
//        if (guangchang==null){
//            mUIDevice.pressBack();
//            guangchang = mUIDevice.findObject(By.text("广场"));
//
//        }
//        guangchang.click();
//        Thread.sleep(2000);
//        mUIDevice.pressBack();
//        UiObject2 close = mUIDevice.findObject(By.text("每日签到"));
//        if(close!=null){
//            Log.d(TAG,"有签到");
//            //close.click();
//            mUIDevice.pressBack();
//        }
    }

}