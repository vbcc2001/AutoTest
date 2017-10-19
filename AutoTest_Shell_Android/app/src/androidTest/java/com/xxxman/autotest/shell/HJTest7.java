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
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;
import android.widget.Toast;

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
public class HJTest7 {

    UiDevice mUIDevice = null;
    Context mContext = null;
    String TAG = "HJTest7";
    String APP = "com.huajiao";
    int log_count = 0;
    SQLUtil7 sqlUtil = new SQLUtil7();
    boolean is_colse_ad = true;
    boolean is4X=Constant.IS_4X();
    String password = "@456123";
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
    //@Test
    public void test_test() {

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test_for() throws Exception {

        String path = Environment.getExternalStorageDirectory().getCanonicalPath();
        List<User> list = FileUtil.ReadTxtFile(path+"/bh_NumberList.txt");
        Log.d(TAG,"user_list.txt中用户数量："+list.size());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        FileUtil.writehengxian(list.size(),path,"chongzhi_"+dateString+".txt");
        String pwd = sqlUtil.selectPassword();
        if(!"".equals(pwd)){
            password = pwd;
        }
        int number = sqlUtil.selectMoney();
        //从指定账号开始，跳过以前的账号
        Log.d(TAG,"跳过账号编号："+number);
        for(int j = 0;j<number-1;j++) {
            list.remove(0);
        }
        boot();
        for(User user:list) {
            Log.d(TAG,user.pwd+"---");
            try {
                test3(user) ;
                FileUtil.writeCZ(user,true,path,"chongzhi_"+dateString+".txt");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FileUtil.writeCZ(user,false,path,"chongzhi_"+dateString+".txt");
                    reboot();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
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
    public void test3(User user) throws Exception {

        login3(user);            //1.登录
        UiObject2 qiandao = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
        if(qiandao!=null){
            qiandao.click();
        }
        Thread.sleep(1000);
        UiObject2 qiandao2 = mUIDevice.findObject(By.res("com.huajiao:id/checkin_btn"));
        if(qiandao2!=null){
            qiandao2.click();
        }
        Thread.sleep(5000);
        mUIDevice.pressBack();
        goWode();         //3.进入我的
        chongzhi();
        quit3();         //8.退出
    }
    public void chongzhi() throws Exception {
        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipe_target"));
        home.flingForward();
        UiObject zhuanghu = mUIDevice.findObject(new UiSelector().text("花椒账户"));
        zhuanghu.click();
        UiObject zhuanghu2 = mUIDevice.findObject(new UiSelector().text("我的账户  (花椒豆)"));
        zhuanghu2.click();
        UiObject jiner = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/charge_value"));
        jiner.setText("1");
        UiObject zhifubao = mUIDevice.findObject(new UiSelector().text("支付宝"));
        zhifubao.click();
        Thread.sleep(2000);
        UiObject fukuang = mUIDevice.findObject(new UiSelector().text("立即付款"));
        fukuang.click();
        Thread.sleep(500);
        //支付密码
        UiObject2 zhifu = mUIDevice.findObject(By.res("com.alipay.android.app:id/simplePwdLayout"));
        if(zhifu!=null){
            zhifu.click();
        }
        char[] a1 = password.toCharArray();
        for(int i=1 ;i<a1.length;i++){
            Thread.sleep(200);
            //4x
            int x=0;
            int y=0;
            //4A
            int x1 =0;
            int y1 = 0;
            switch (""+a1[i]){
                case "0":x=540;y=1840;x1=360;y1=1230;break;
                case "1": x=200;y=1360;x1=120;y1=910;break;
                case "2": x=550;y=1360;x1=360;y1=910;break;
                case "3": x=900;y=1360;x1=600;y1=910;break;
                case "4": x=200;y=1520;x1=120;y1=1010;break;
                case "5": x=550;y=1520;x1=360;y1=1010;break;
                case "6": x=900;y=1520;x1=600;y1=1010;break;
                case "7": x=200;y=1680;x1=120;y1=1120;break;
                case "8": x=550;y=1680;x1=360;y1=1120;break;
                case "9": x=900;y=1680;x1=600;y1=1120;break;
            }
            if(is4X){
                mUIDevice.click(x,y);
            }else{
                mUIDevice.click(x1,y1);
            }
        }
        Thread.sleep(8000);
        mUIDevice.pressBack();
        mUIDevice.pressBack();
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
    }
    //进入广场
    public void goWode() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        try{
            UiObject guangchang = mUIDevice.findObject(new UiSelector().text("我的"));
            guangchang.click();
        }catch(Exception e) {
            reboot();
            UiObject guangchang = mUIDevice.findObject(new UiSelector().text("我的"));
            guangchang.click();
        }
    }

}