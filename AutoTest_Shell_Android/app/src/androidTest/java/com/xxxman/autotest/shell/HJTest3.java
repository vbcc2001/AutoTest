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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class HJTest3{

    UiDevice mUIDevice = null;
    String TAG = "HJTest3";
    String APP = "com.huajiao";
    int log_count = 0;
    Context mContext = null;
    int count_get_hongbao = 0;
    SQLUtil1 sqlUtil = new SQLUtil1();
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
        MyConnection my  = new MyConnection();
        String url = "http://vpn.m2ss.top:3000/action/lfs/action/FunctionAction";
        Map<String,String> parms = new HashMap<>();
        String phone = sqlUtil.selectCode();
        if (phone.length()==13){
            phone = phone.substring(1);
        }
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath();
            List<User> list = sqlUtil.selectHongbaoUser();
            FileUtil.writehengxian(list.size(),path,"hongbao_"+sqlUtil.dateString+".txt");
            //提醒
            Intent intent = new Intent();
            intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
            intent.putExtra("name", "抢红包任务开启");
            mContext.sendBroadcast(intent);
            int num =0;
            for(User user:list) {
                //执行任务
                sqlUtil.updateHongbaoTaskCount(user);
                Log.d(TAG, user.pwd + "---");
                num++;
                intent.putExtra("name", "开始执行"+num+"/"+list.size()+"个任务（第1次循环）");
                mContext.sendBroadcast(intent);
                test_for(user);
                FileUtil.writeHongbao(user,path,"hongbao_"+sqlUtil.dateString+".txt");
                //完成任务
                String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"dou\\\":"+user.dou+"}\"";
                String context = "{\"function\":\"F100005\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";

                parms.put("jsonContent",context);
                String rs = my.getContextByHttp(url,parms);
                Log.d(TAG,"http请求结果"+rs);
            }

            for(int i = 0 ;i<100;i++){
                list = sqlUtil.selectHongbaoFailUser();
                FileUtil.writehengxian(list.size(),path,"hongbao_"+sqlUtil.dateString+".txt");
                num = 0;
                for(User user:list) {
                    num++;
                    intent.putExtra("name", "开始执行"+num+"/"+list.size()+"个任务（第"+(i+2)+"/100次循环）");
                    mContext.sendBroadcast(intent);
                    test_for(user);
                    FileUtil.writeHongbao(user,path,"hongbao_"+sqlUtil.dateString+".txt");
                    //完成任务
                    String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"dou\\\":"+user.dou+"}\"";
                    String context = "{\"function\":\"F100005\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";

                    parms.put("jsonContent",context);
                    String rs = my.getContextByHttp(url,parms);
                    Log.d(TAG,"http请求结果"+rs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //@Test
    public void test_for(User user){

        //List<User> list = sqlUtil.selectLoginCount();
        try {
            if(user!=null) {
                count_get_hongbao = 0 ;
                login(user);
                for (int i = 0; i < 6; i++) {
                    try {
                        if (i==0 || i==2 || i==4){
                            find_money(user,"最新");
                        }
                        if (i==5){

                            UiObject2 city = mUIDevice.findObject(By.text("深圳"));
                            if (city!=null){
                                find_money(user,"深圳");
                            }
                            city = mUIDevice.findObject(By.text("北京"));
                            if (city!=null){
                                find_money(user,"北京");
                            }
                            city = mUIDevice.findObject(By.text("上海"));
                            if (city!=null){
                                find_money(user,"上海");
                            }
                            city = mUIDevice.findObject(By.text("广州"));
                            if (city!=null){
                                find_money(user,"广州");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reboot();
                    }
                    if (count_get_hongbao>=3){
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
    public void find_money(User user,String menu) throws Exception {


        //UiObject new_list = mUIDevice.findObject(new UiSelector().text("最新"));
        UiObject new_list = mUIDevice.findObject(new UiSelector().text(menu));
        new_list.click();
        for(int i =0 ;i < 18 ;i++){
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
                for(int j =0 ;j < 30 ;j++){
                    if(is4X){
                        mUIDevice.click(954,1367);
                    }else{
                        mUIDevice.click(640,910);
                    }
                    Thread.sleep(1000);
                    UiObject2 kaihongbao = mUIDevice.findObject(By.res("com.huajiao:id/pre_btn_open"));
                    //情况1：成功
                    if(kaihongbao!=null){
                        count_get_hongbao++;
                        user.hongbao = sqlUtil.updateHongbaoCount(user);
                        if(is4X){
                            mUIDevice.click(990,1843);
                            mUIDevice.click(990,1843);
                        }else{
                            mUIDevice.click(660,1228);
                            mUIDevice.click(660,1228);
                        }

                        break;
                    }else{
                        //情况2：失败
                        UiObject2 wuyuan = mUIDevice.findObject(By.text("和红包无缘相遇，期待下次好运吧~"));
                        if(wuyuan!=null){
                            if(is4X){
                                mUIDevice.click(990,1843);
                                mUIDevice.click(990,1843);
                            }else{
                                mUIDevice.click(660,1228);
                                mUIDevice.click(660,1228);
                            }
                            break;
                        }else{
                            //情况3：失败
                            UiObject2 meiyou = mUIDevice.findObject(By.text("没抢到红包，肯定是抢的姿势不对~"));
                            if(meiyou!=null){
                                if(is4X){
                                    mUIDevice.click(990,1843);
                                    mUIDevice.click(990,1843);
                                }else{
                                    mUIDevice.click(660,1228);
                                    mUIDevice.click(660,1228);
                                }
                                break;
                            }else{
                                UiObject2 yunqicha = mUIDevice.findObject(By.text("运气不佳，没抢到红包~"));
                                if(yunqicha!=null){
                                    if(is4X){
                                        mUIDevice.click(990,1843);
                                        mUIDevice.click(990,1843);
                                    }else{
                                        mUIDevice.click(660,1228);
                                        mUIDevice.click(660,1228);
                                    }
                                    break;
                                }else{
                                    //情况4：未完成
                                    UiObject2 hongdou100 = mUIDevice.findObject(By.text("给钱也不要"));
                                    if(hongdou100!=null){
                                        if(is4X){
                                            mUIDevice.click(990,1770);
                                        }else{
                                            mUIDevice.click(660,1180);
                                        }
                                    }else{
                                        //没红包
                                        //if(j>2){
                                            if(is4X){
                                                mUIDevice.click(990,1843);
                                                mUIDevice.click(990,1843);
                                            }else{
                                                mUIDevice.click(660,1228);
                                                mUIDevice.click(660,1228);
                                            }
                                            break;
                                        //}

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
}

