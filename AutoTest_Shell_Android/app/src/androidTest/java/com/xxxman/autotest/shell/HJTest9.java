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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class HJTest9 {

    UiDevice mUIDevice = null;
    String TAG = "HJTest9";
    String APP = "com.huajiao";
    int log_count = 0;
    Context mContext = null;
    SQLUtil9 sqlUtil9 = new SQLUtil9();
    Order order = new Order();
    String path = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

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
    public void test(){
        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
        try {
            order = sqlUtil9.selectOrder();
            List<User> list = sqlUtil9.selectSunUser(order);
            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
            FileUtil.writehengxian(list.size(),path,"send_sun_"+sqlUtil9.dateString+".txt");
            //从指定账号开始，跳过以前的账号
            for(int j = 0;j<order.begin_accout-1;j++) {
                list.remove(0);
            }
            for(int i = 0; i<list.size(); i++){
                User user = list.get(i);
                Intent intent = new Intent();
                intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
                intent.putExtra("name", "送阳光任务开启("+(i+1)+"／"+list.size()+"个)，任务编号" +order.id);
                mContext.sendBroadcast(intent);
                //执行任务
                test_for(user);
                intent.putExtra("name", "送阳光任务完成。");
                mContext.sendBroadcast(intent);
                Log.d(TAG, "送阳光任务完成。");
            }
            mUIDevice.pressHome();  //按home键
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_for(User user) {

        try {
            if(user!=null) {
                login(user);
                Thread.sleep(1000);
                mUIDevice.pressBack();
                Thread.sleep(3000);
                mUIDevice.pressBack();
                Thread.sleep(3000);
                mUIDevice.pressBack();
                search(""+order.huajiao_id,user);
                songSun(user);
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
        UiObject2 login = mUIDevice.findObject(By.text("手机号登录"));
        if(login==null){
            quit(user);
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
    public void quit(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiObject2 huajiao_id = mUIDevice.findObject(By.res("com.huajiao:id/number_view"));
        if(huajiao_id!=null){
            user.pwd = huajiao_id.getText();
            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
            FileUtil.writeSendSun(user,order,path,"send_sun_"+sqlUtil9.dateString+".txt");
        }
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

    public void search(String id,User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());


        UiObject chaxun = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/explore_search_btn"));
        chaxun.click();
        UiObject huajiao_id = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/edit_keyword"));
        huajiao_id.setText(id);
        Thread.sleep(500);
        UiObject sousuo = mUIDevice.findObject(new UiSelector().text("搜索"));
        sousuo.click();
        Thread.sleep(1000);
        sousuo.click();
        Thread.sleep(3000);
        UiObject2 guangzhu = mUIDevice.findObject(By.res("com.huajiao:id/focus_iv"));
        if(guangzhu!=null){
            guangzhu.click();
        }
        UiObject touxiang = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/search_item_user_icon"));
        touxiang.click();
        Thread.sleep(2000);
        UiObject2 zhibozhong = mUIDevice.findObject(By.res("com.huajiao:id/icon_view"));
        if(zhibozhong!=null){
            zhibozhong.click();
        }
    }

    public void songSun(User user) throws Exception {


        for(int j=0;j<10;j++){
            Thread.sleep(2000);
            if(Constant.IS_4X){
                mUIDevice.click(540,1840);
                Thread.sleep(500);
                mUIDevice.click(116,1242);
            }else{
                mUIDevice.click(360,1228);
                Thread.sleep(500);
                mUIDevice.click(80,832);
            }
            Thread.sleep(500);
            UiObject doushu = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/tv_text_num_sun"));
            //第一次记录下送阳光数
            if(j==0){
                if(order.is_sun){
                    user.send_dou = (Integer.valueOf(doushu.getText())/100)*100;
                }else if(order.is_xrk){
                    user.send_dou = (Integer.valueOf(doushu.getText())/1800)*1800;
                }
            }
            user.dou = Integer.valueOf(doushu.getText());
            if((user.dou/1800)>=1 && order.is_xrk){
                //先送一个
                UiObject2 liwu = mUIDevice.findObject(By.text("1800阳光"));
                if(!liwu.getParent().isSelected()){
                    liwu.click();
                }
                for(int i = 0 ;i<(user.dou/1800);i++){
                    UiObject2 send = mUIDevice.findObject(By.text("发送"));
                    send.click();
                    Thread.sleep(5000);
                    if(Constant.IS_4X){
                        mUIDevice.click(540,1840);
                        Thread.sleep(500);
                        mUIDevice.click(116,1242);
                    }else{
                        mUIDevice.click(360,1228);
                        Thread.sleep(500);
                        mUIDevice.click(80,832);
                    }
                }
            }else if((user.dou/100)>=1 && order.is_sun){
                UiObject2 liwu = mUIDevice.findObject(By.text("100阳光"));
                if(!liwu.getParent().isSelected()){
                    liwu.click();
                }
                UiObject2 send = mUIDevice.findObject(By.text("发送"));
                send.click();
                for(int i = 0 ;i<(user.dou/100);i++){
                    if(Constant.IS_4X){
                        mUIDevice.click(990,1843);
                    }else{
                        mUIDevice.click(660,1228);
                    }
                    Thread.sleep(500);
                }

            }else{
                break;
            }
            //mUIDevice.pressBack();
            Thread.sleep(1000);

        }
        //关闭直播
        if(Constant.IS_4X){
            mUIDevice.click(30,70);
            mUIDevice.click(30,70);
            mUIDevice.click(990,1843);
        }else{
            mUIDevice.click(30,70);
            mUIDevice.click(30,70);
            mUIDevice.click(660,1228);
        }
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
    }
}

