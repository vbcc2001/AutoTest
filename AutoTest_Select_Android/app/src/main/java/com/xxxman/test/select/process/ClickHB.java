package com.xxxman.test.select.process;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.DataRow;
import com.xxxman.test.select.object.HttpRequest;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.sql.TaskSQL;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.SQLUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClickHB {
    private static final String TAG = SelectHB.class.getName();
    String APP = "com.huajiao";
    int log_count = 0;
    int fail_count = 0;
    UiDevice mUIDevice = null;
    Context mContext = null;
    String taskType = "hongbao";
    boolean is4X = Constant.IS_4X;

    @Test
    public void test() {
        mUIDevice.pressHome();  //按home键
        try {
            Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
            mContext.startActivity(myIntent);
            mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
            mUIDevice.pressBack();
            Thread.sleep(3000);
            mUIDevice.pressBack();
            if(!SQLUtil.tabbleIsExist(TaskSQL.table)){
                TaskSQL.createTableTask();
            }
            if(TaskSQL.selectTaskCount(taskType)==0){
                for (int i = 0; i < 10; i++) {
                    Map<String,String> para = new HashMap<>();
                    para.put("phone","c4c8ba9f4fd2");
                    para.put("type","hongbao");
                    HttpResult httpResult = HttpUtil.post("F100010",para);
                    if("".equals(httpResult.getErrorNo())) {
                        List<Task> list = new ArrayList<Task>();
                        List<DataRow> list_dataRow =  httpResult.getList();
                        for(DataRow dataRow : list_dataRow){
                            Task task = new Task();
                            task.setNumber(dataRow.getInt("number"));
                            task.setUid(0);
                            task.setPhone(dataRow.getString("accout"));
                            task.setPwd(dataRow.getString("pwd"));
                            task.setDay("");
                            task.setTask_count(Constant.HONGBAO_COUNT);
                            task.setSuccess_count(0);
                            task.setType(taskType);
                            list.add(task);
                        }
                        TaskSQL.inserTask(list,taskType);
                        break;
                    }else{
                        Log.e(TAG,"获取用户信息失败："+httpResult.getErrorInfo());
                        Thread.sleep(5000);
                    }
                }
            }
            List<Task> list = TaskSQL.selectTask(taskType);
            if(list.size()>0){
                for (int j = 0; j < 100; j++) {
                    for(Task task :list){
                        qiangHongBao(task);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
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
                return false;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
    //关闭弹窗广告
    public void closeAd() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject2 close = mUIDevice.findObject(By.res("com.huajiao:id/img_close"));
        if(close!=null){
            Log.d(TAG,"有广告");
            close.click();
        }
    }
    //退出流程
    public void quit() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.scrollToEnd(1);
        UiObject2 dou = mUIDevice.findObject(By.res("com.huajiao:id/hjd_num_tv"));
        UiObject setting = mUIDevice.findObject(new UiSelector().text("设置"));
        setting.click();
        UiObject2 set = mUIDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(1);
        set.scroll(Direction.DOWN, 0.8f);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();
    }
    //登录流程
    public void login(Task task) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
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
        phone.setText(task.getPhone());
        UiObject password = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/pwd_et"));
        password.setText(task.getPwd());
        UiObject logining = mUIDevice.findObject(new UiSelector().text("登录"));
        logining.click();
    }
    //抢红包
    public void qiangHongBao(Task task)   {
        try{
            login(task);
            for(int j=0;j<100;j++){
                try{
                    HttpResult httpResult = HttpUtil.post("F200101");
                    if("".equals(httpResult.getErrorNo())) {
                        List<DataRow> list_dataRow =  httpResult.getList();
                        int size =5;
                        if(list_dataRow.size()<size){
                            size = list_dataRow.size();
                        }
                        for(int i=0;i<size;i++){
                            try {
                                String uid = list_dataRow.get(i).getString("uid");
                                goZhiBo(uid);
                                share();
                                click();
                                closeZhiBo();
                            }catch (Exception e){
                                e.printStackTrace();
                                reboot();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    reboot();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            reboot();
        }
    }
    //重启
    public void reboot() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(APP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
    }
    //进入直播
    public void goZhiBo(String uid) throws Exception {
        UiObject chaxun = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/explore_search_btn"));
        chaxun.click();
        UiObject huajiao_id = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/edit_keyword"));
        huajiao_id.setText(uid);
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
    //点击
    public void click() throws Exception {
        for(int j =0 ;j < 50 ;j++){
            if(is4X){
                mUIDevice.click(954,1367);
            }else{
                mUIDevice.click(640,910);
            }
            Thread.sleep(500);
            UiObject2 kaihongbao = mUIDevice.findObject(By.res("com.huajiao:id/pre_btn_open"));
            //情况1：成功
            if(kaihongbao!=null){
                break;
            }else{
                //情况2：失败
                UiObject2 wuyuan = mUIDevice.findObject(By.text("和红包无缘相遇，期待下次好运吧~"));
                if(wuyuan!=null){
                    fail_count++;
                    break;
                }else{
                    //情况3：失败
                    UiObject2 meiyou = mUIDevice.findObject(By.text("没抢到红包，肯定是抢的姿势不对~"));
                    if(meiyou!=null){
                        fail_count++;
                        break;
                    }else{
                        UiObject2 yunqicha = mUIDevice.findObject(By.text("运气不佳，没抢到红包~"));
                        if(yunqicha!=null){
                            fail_count++;
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
                                //情况5：未实名
                                UiObject2 renzheng = mUIDevice.findObject(By.text("实名认证提示"));
                                if(renzheng!=null){
                                    throw new Exception("未实名认证");
                                }else {
                                    UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
                                    if(login!=null){
                                        throw new Exception("未登录");
                                    }else{
                                        //没红包
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
    //退出直播
    public void closeZhiBo() throws Exception {
        if(is4X){
            mUIDevice.click(990,1843);
            mUIDevice.click(990,1843);
        }else{
            mUIDevice.click(660,1228);
            mUIDevice.click(660,1228);
        }
        mUIDevice.pressBack();
        mUIDevice.pressBack();
        mUIDevice.pressBack();
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
