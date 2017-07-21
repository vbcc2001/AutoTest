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
public class HJTest4 {

    private UiDevice mUIDevice = null;
    String TAG = "HJTest3";
    String APP = "com.huajiao";
    int log_count = 0;
    private Context mContext = null;
    int count_get_hongbao = 0;
    SQLUtil sqlUtil = new SQLUtil();
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("notifation", new UiWatcher() {

            @Override
            public boolean checkForCondition() {
                Log.d(TAG,":进入Watcher");
                try {
                    closeAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
    @Test
    public void test_for(){

        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);


        List<User> list = sqlUtil.selectLoginCount();
        try {
            User user = list.get(0);
            if(user!=null) {
                login(user);
                for (int i = 0; i < 25; i++) {
                    try {
                        find_money(list.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reboot();
                    }
                    if (count_get_hongbao>=6){
                        break;
                    }
                }
                quit(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
        }
    }
    public void reboot() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(APP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
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
    //登录流程
    public void login(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
        if(login==null){
            quit(user);
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
    public void quit(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        //UiObject my_page = mUIDevice.findObject(new UiSelector().text("我的主页"));
        //my_page.click();

        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.scrollToEnd(1);
        //mUIDevice.swipe(100, 1676, 100, 600, 20);
        UiObject2 dou = mUIDevice.findObject(By.res("com.huajiao:id/hjd_num_tv"));
        if (dou != null){
            Log.d(TAG,dou.getText()+"---------");
            user.dou = Integer.valueOf(dou.getText());
        }
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
    public void find_money(User user) throws Exception {


        UiObject new_list = mUIDevice.findObject(new UiSelector().text("最新"));
        new_list.click();
        for(int i =0 ;i < 25 ;i++){
            if(i>0){
                UiObject list = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/listview"));
                list.swipeUp(50);
                Log.d(TAG,"-----");
                Thread.sleep(1000);
            }
            UiObject2 money = mUIDevice.findObject(By.res("com.huajiao:id/hongbao"));
            if(money!=null){
                Log.d(TAG,"youhongbao");
                money.click();
                Thread.sleep(1000);
                share();
                for(int j =0 ;j < 25 ;j++){
                    mUIDevice.click(954,1367);
                    Thread.sleep(500);
                    UiObject2 kaihongbao = mUIDevice.findObject(By.res("com.huajiao:id/pre_btn_open"));
                    //情况1：成功
                    if(kaihongbao!=null){
                        count_get_hongbao++;
                        user.hongbao = sqlUtil.updateHongbaoCount(user);
                        mUIDevice.click(990,1843);
                        mUIDevice.click(990,1843);
                        break;
                    }else{
                        //情况2：失败
                        UiObject2 wuyuan = mUIDevice.findObject(By.text("和红包无缘相遇，期待下次好运吧~"));
                        if(wuyuan!=null){
                            mUIDevice.click(990,1843);
                            mUIDevice.click(990,1843);
                            break;
                        }else{
                            //情况3：失败
                            UiObject2 meiyou = mUIDevice.findObject(By.text("没抢到红包，肯定是抢的姿势不对~"));
                            if(meiyou!=null){
                                mUIDevice.click(990,1843);
                                mUIDevice.click(990,1843);
                                break;
                            }else{
                                UiObject2 yunqicha = mUIDevice.findObject(By.text("运气不佳，没抢到红包~"));
                                if(yunqicha!=null){
                                    mUIDevice.click(990,1843);
                                    mUIDevice.click(990,1843);
                                    break;
                                }else{
                                    //情况4：未完成
                                    UiObject2 hongdou100 = mUIDevice.findObject(By.text("100豆红包等你来抢"));
                                    if(hongdou100!=null){
                                        mUIDevice.click(990,1770);
                                        for(int jj =0 ;jj < 5 ;jj++) {
                                            Thread.sleep(200);
                                        }
                                    }else{
                                        //没红包
                                        mUIDevice.click(990,1843);
                                        mUIDevice.click(990,1843);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void share() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        Thread.sleep(2000);
        mUIDevice.click(840,1843);
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

