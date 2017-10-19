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
import android.text.LoginFilter;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class HJTest5 {

    UiDevice mUIDevice = null;
    String TAG = "HJTest5";
    String APP = "com.huajiao";
    int log_count = 0;
    Context mContext = null;
    int count_get_hongbao = 0;
    SQLUtil2 sqlUtil2 = new SQLUtil2();
    Order order = new Order();
    String path = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    int sum_dou = 0;
    //记录以前送的豆
    int before_send_dou = 0 ;
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
        order = sqlUtil2.selectOrder();
        if (order.id>0){
            List<User> list = sqlUtil2.selectDouUser(order);

            sum_dou = sqlUtil2.selectSendDou(order.id);
            //从指定账号开始，跳过以前的账号
            for(int j = 0;j<order.begin_accout-1;j++) {
                list.remove(0);
            }
            for(int i = 0; i<list.size(); i++){
                User user = list.get(i);
                before_send_dou = user.send_dou;
                Intent intent = new Intent();
                intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
                intent.putExtra("name", "送豆任务开启("+(i+1)+"／"+list.size()+"个)，任务编号"
                        +order.id+",当前账户送豆数("+user.send_dou+"/"+order.per_dou+"),总送豆数("+sum_dou+"/"+order.max_dou+")");
                mContext.sendBroadcast(intent);
                if(sum_dou>=order.max_dou){
                    Log.d(TAG, "送豆数"+sum_dou+"超过总送豆数"+order.max_dou+"，任务完成！");
                    break;
                }
                //执行任务
                try {
                    test_for(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent();
            intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
            intent.putExtra("name", "送豆任务完成，总送豆数为："+sum_dou);
            mContext.sendBroadcast(intent);
            Log.d(TAG, "送豆任务完成，总送豆数为："+sum_dou);
//                path = Environment.getExternalStorageDirectory().getCanonicalPath();
//                String dateString = formatter.format(new Date());
//                FileUtil.writehengxian(list.size(),path,"dou_"+dateString+".txt");
//                String dateString = formatter.format(new Date());
//                user.send_dou = user.send_dou-before_send_dou;
//                FileUtil.writeDou(user,order,path,"dou_"+dateString+".txt");

        }else{
            Intent intent = new Intent();
            intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
            intent.putExtra("name", "送豆任务编号错误："+order.id);
            mContext.sendBroadcast(intent);
        }
        mUIDevice.pressHome();  //按home键
    }

    public void test_for(User user) throws Exception {

        try {
            if(user!=null) {
                login(user);
                search(""+order.huajiao_id,user);
                user.last_dou = quit();
                //修正送豆
                if((user.dou-user.last_dou)>=0 && (user.send_dou-before_send_dou)>=0) {
                    if ((user.dou - user.last_dou) != (user.send_dou - before_send_dou)) {
                        user.send_dou = user.dou - user.last_dou + before_send_dou;
                    }
                }
                sqlUtil2.updateDou(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
            user.last_dou = quit();
            //修正送豆
            if((user.dou-user.last_dou)>=0 && (user.send_dou-before_send_dou)>=0) {
                if ((user.dou - user.last_dou) != (user.send_dou - before_send_dou)) {
                    user.send_dou = user.dou - user.last_dou + before_send_dou;
                }
            }
            sqlUtil2.updateDou(user);
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
    public int quit() throws Exception {
        int last_dou=-1;
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
            last_dou = Integer.valueOf(dou.getText());
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
        return last_dou;
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
            Log.d(TAG,"暖场时间"+order.wait_time);
            if(order.wait_time>0){
                hot(user);
            }
            songLiWu(user);
        }
    }

    public void songLiWu(User user) throws Exception {

        Thread.sleep(5000);
        if(Constant.IS_4X()){
            mUIDevice.click(540,1840);
        }else{
            //mUIDevice.click(560,1228);
        }
        Thread.sleep(1000);
        UiObject doushu = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/tv_text_num"));
        user.dou = Integer.valueOf(doushu.getText());
        if(user.dou>0){
            //随机选择1或2个豆的礼物
            int dou = 1;
//            Random random=new Random();// 定义随机类
//            int y =random.nextInt(2)+1;// 返回[0,2)集合中的整数，注意不包括2
            //双数豆送2个豆礼物
            if(user.dou>=2 && (user.dou%2)==0){
                dou = 2;
            }
            //先送一个
            UiObject2 liwu = mUIDevice.findObject(By.text(dou+"豆"));
            if(!liwu.getParent().isSelected()){
                liwu.click();
            }
            UiObject2 send = mUIDevice.findObject(By.text("发送"));
            send.click();
            //记录送豆信息
            user.send_dou = user.send_dou+dou;
            sum_dou = sum_dou+dou;
            Log.d(TAG,"第一次按送豆："+user.send_dou+"，总送豆数："+sum_dou);
            Thread.sleep(500);
            if(sum_dou<order.max_dou && user.send_dou < order.per_dou){
                for(int j=0 ;j<user.dou ;j=j+dou){
                    if(Constant.IS_4X()){
                        mUIDevice.click(990,1843);
                    }else{
                        mUIDevice.click(660,1228);
                    }
                    Thread.sleep(500);
                    user.send_dou = user.send_dou+dou;
                    sum_dou = sum_dou+dou;
                    Log.d(TAG,"第"+j+"次按送豆："+user.send_dou+"，总送豆数："+sum_dou);
                    if(sum_dou>=order.max_dou  || user.send_dou >= order.per_dou){
                        break;
                    }
                }
            }
            Log.d(TAG,"该账户总豆数（修正前）："+user.send_dou);
            UiObject2 yuerbuzu = mUIDevice.findObject(By.text("余额不足"));
            if(yuerbuzu!=null){
                UiObject2 quxiao = mUIDevice.findObject(By.text("取消"));
                if(quxiao!=null){
                    quxiao.click();
                }
            }
            Thread.sleep(2000);
            UiObject doushu1 = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/tv_text_num"));
            user.last_dou = Integer.valueOf(doushu1.getText());
            user.send_dou = user.dou-user.last_dou+before_send_dou;
            sqlUtil2.updateDou(user);
            Intent intent = new Intent();
            intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
            intent.putExtra("name", "第"+user.number+"个账户送豆完成，送出"+(user.send_dou-before_send_dou)+"个，剩余"+(user.last_dou)+"个");
            mContext.sendBroadcast(intent);
            //关闭直播
            if(Constant.IS_4X()){
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
    public void hot(User user) throws Exception {
        String[] talk_list = new String[]{"你好","截图给我看看","我关注了主播","偏偏靠才艺"};
        Thread.sleep(5000);
        if(Constant.IS_4X()){
            mUIDevice.click(88,1844);
        }else{
        }
        Log.d(TAG,"是否聊天"+order.is_talk);
        for(int i = 0 ;i<order.wait_time*3;i++){

            if(order.is_talk){
                Thread.sleep(15000);
                UiObject talk = mUIDevice.findObject(new UiSelector().text("说点什么吧..."));
                Random random=new Random();// 定义随机类
                int y =random.nextInt(talk_list.length);// 返回[0,2)集合中的整数，注意不包括2
                talk.setText(talk_list[y]);
                UiObject send = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_send"));
                send.click();
            }else{
                Thread.sleep(20000);
            }
        }
        mUIDevice.pressBack();
    }
}

