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
public class HJTest5 {

    UiDevice mUIDevice = null;
    String TAG = "HJTest5";
    String APP = "com.huajiao";
    int log_count = 0;
    Context mContext = null;
    int count_get_hongbao = 0;
    SQLUtil2 sqlUtil2 = new SQLUtil2();
    boolean is4X=true;
    Order order = new Order();

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


        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath();
            order = sqlUtil2.selectOrder();
            if (order.id>0){
                List<User> list = sqlUtil2.selectHongbaopUser(0);
                for(User user:list) {
                    //执行任务
                    test_for(user);
                }
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void test_for(User user){

        try {
            if(user!=null) {
                login(user);
                songHongbao("66224999",user);
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
    public void songHongbao(String id,User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());


        UiObject chaxun = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/explore_search_btn"));
        chaxun.click();
        UiObject huajiao_id = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/edit_keyword"));
        huajiao_id.setText(id);
        Thread.sleep(500);
        UiObject sousuo = mUIDevice.findObject(new UiSelector().text("搜索"));
        sousuo.click();

        UiObject2 guangzhu = mUIDevice.findObject(By.res("com.huajiao:id/focus_iv"));
        if(guangzhu!=null){
            guangzhu.click();
        }
        UiObject touxiang = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/search_item_user_icon"));
        touxiang.click();

        UiObject2 zhibozhong = mUIDevice.findObject(By.res("com.huajiao:id/icon_view"));
        if(zhibozhong!=null){
            zhibozhong.click();
        }else{
            //
        }

        Thread.sleep(2000);
        if(is4X){
            mUIDevice.click(540,1840);
        }else{
            //mUIDevice.click(560,1228);
        }

        UiObject doushu = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/tv_text_num"));
        user.dou = Integer.valueOf(doushu.getText());
        for(int i=0;i<2;i++){
            UiObject2 liwu = mUIDevice.findObject(By.text("1豆"));
            if(liwu==null){
                liwu = mUIDevice.findObject(By.text("2豆"));
                i++;
            }
            if(!liwu.getParent().isSelected()){
                liwu.click();
            }
            UiObject2 send = mUIDevice.findObject(By.text("发送"));
            send.click();
            Thread.sleep(5000);
        }
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
    }
}

