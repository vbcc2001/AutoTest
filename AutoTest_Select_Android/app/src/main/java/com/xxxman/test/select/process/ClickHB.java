package com.xxxman.test.select.process;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.DataRow;
import com.xxxman.test.select.object.HttpRequest;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.sql.TaskSQL;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.RSAUtils;
import com.xxxman.test.select.util.SNUtil;
import com.xxxman.test.select.util.SQLUtil;
import com.xxxman.test.select.util.ToastUitl;

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
    int vpn_quit_count = 0;
    UiDevice mUIDevice = null;
    Context mContext = null;
    String taskType = "hongbao";
    boolean is4X = Constant.IS_4X;
    String sn_code = "";

    @Test
    public void test() {
        mUIDevice.pressHome();  //按home键
        try {
            Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
            mContext.startActivity(myIntent);
            mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
            Thread.sleep(3000);
            mUIDevice.pressBack();
            Thread.sleep(3000);
            mUIDevice.pressBack();
            if(!SQLUtil.tabbleIsExist(TaskSQL.table)){
                TaskSQL.createTableTask();
            }
            if(TaskSQL.selectTaskCount(taskType)==0){
                for (int i = 0; i < 10; i++) {
                    Map<String,String> para = new HashMap<>();
                    SharedPreferences preferences= mContext.getSharedPreferences("sn_code", Context.MODE_PRIVATE);
                    sn_code =preferences.getString("sn_code", "xxx");
                    Log.d(TAG,"注册码为："+sn_code);
                    para.put("phone",sn_code);
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
                            //task.setPwd(dataRow.getString("pwd"));
                            task.setPwd("qaz147258..");
                            task.setDay("");
                            task.setTask_count(0);
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
                for (int j = 0; j < 6; j++) {
                    for(Task task :list){
                        Log.d(TAG,"开始执行第（"+task.getNumber()+"）任务："
                                +"phone="+task.getPhone()
                                +"，task_count="+task.getTask_count()
                                +"，success_count="+task.getSuccess_count()
                                +"，fail_count="+task.getFail_count());
                        //没抢满红包
                        if(task.getSuccess_count()<Constant.HONGBAO_COUNT ){
                            //执行小于6次
                            if( task.getTask_count()<j+1 ){
                                //失败小于6次
                                if( task.getFail_count()< 6 ){
                                    //失败小于3次，或者失败小于6次但成功小于3次
                                    if(task.getFail_count()< 3 || task.getSuccess_count()<3){
                                        //提醒
                                        String info = "开始第（"+task.getNumber()+"）用户："
                                                +"，第"+task.getTask_count()+"次执行,"
                                                +"，已抢到"+task.getSuccess_count()+"次,"
                                                +"，未抢到"+task.getFail_count()+"次；";
                                        //提示
                                        ToastUitl.sendBroadcast(mContext,info);
                                        qiangHongBao(task);

                                    }
                                }
                            }
                        }
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
    public void quit(Task task,boolean is_record_dou) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.scrollToEnd(1);
        if(is_record_dou){
            UiObject2 dou = mUIDevice.findObject(By.res("com.huajiao:id/hjd_num_tv"));
            if (dou != null){
                Log.d(TAG,"豆数为："+dou.getText()+"---------");
                int dou_sum = Integer.valueOf(dou.getText());
                if(dou_sum>0){
                    Map<String,String> map = new HashMap<>();
                    map.put("phone",sn_code);
                    map.put("account",task.getPhone());
                    map.put("state","1");
                    map.put("pwd","*");
                    map.put("dou",task.getPhone());
                    HttpResult httpResult = HttpUtil.post("F100005",map);
                }
            }
        }
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
            quit(task,false);
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
        ToastUitl.sendBroadcast(mContext,"正在登录第"+task.getNumber()+"个用户");
    }
    //抢红包
    public void qiangHongBao(Task task)   {
        try{
            changeIP();
            login(task);
            TaskSQL.updateTaskCount(task.getId(),"task_count",task.getTask_count()+1);
            for(int j=0;j<100;j++){
                try{
                    HttpResult httpResult = HttpUtil.post("F200101");
                    if("".equals(httpResult.getErrorNo())) {
                        List<DataRow> list_dataRow =  httpResult.getList();
                        int size =3;
                        if(list_dataRow.size()<size){
                            size = list_dataRow.size();
                        }
                        for(int i=0;i<size;i++){
                            try {
                                String uid = list_dataRow.get(i).getString("uid");
                                goZhiBo(uid);
                                Thread.sleep(3000);
                                share();
                                click(task);
                                closeZhiBo();
                                if(task.getFail_count()>=3){
                                    break;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                reboot();
                            }
                        }
                    }else{
                        Thread.sleep(5000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    reboot();
                }
                if(task.getFail_count()>=3){
                    break;
                }
                if(task.getSuccess_count()>=Constant.HONGBAO_COUNT_ONE && task.getTask_count()==0){
                    break;
                }
                if(task.getSuccess_count()>=Constant.HONGBAO_COUNT && task.getTask_count()>1){
                    break;
                }
            }
            quit(task,true);
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
        Thread.sleep(1000);
        UiObject sousuo = mUIDevice.findObject(new UiSelector().text("搜索"));
        sousuo.click();
        Thread.sleep(2000);
        UiObject2 touxiang = mUIDevice.findObject(By.res("com.huajiao:id/search_item_user_icon"));
        if(touxiang==null){
            sousuo.click();
            Thread.sleep(3000);
            touxiang = mUIDevice.findObject(By.res("com.huajiao:id/search_item_user_icon"));
        }
        touxiang.click();
        Thread.sleep(2000);
        UiObject2 zhibozhong = mUIDevice.findObject(By.res("com.huajiao:id/icon_view"));
        if(zhibozhong!=null){
            zhibozhong.click();
        }
    }
    //点击
    public void click(Task task) throws Exception {
        for(int j =0 ;j < 50 ;j++){
            if(is4X){
                mUIDevice.click(954,1367);
            }else{
                mUIDevice.click(640,910);
            }
            Thread.sleep(500);
            UiObject2 hongdou100 = mUIDevice.findObject(By.text("给钱也不要"));
            if(hongdou100!=null) {
                if (is4X) {
                    mUIDevice.click(990, 1770);
                } else {
                    mUIDevice.click(660, 1180);
                }
                continue;
            }else{
                Thread.sleep(2000);
            }
            UiObject2 kaihongbao = mUIDevice.findObject(By.res("com.huajiao:id/pre_btn_open"));
            //情况1：成功
            if(kaihongbao!=null){
                kaihongbao.click();
                Thread.sleep(2000);
                task.setSuccess_count(task.getSuccess_count()+1);
                TaskSQL.updateTaskCount(task.getId(),"success_count",task.getSuccess_count());
                break;
            }else{
                //情况2：失败
                UiObject2 wuyuan = mUIDevice.findObject(By.text("和红包无缘相遇，期待下次好运吧~"));
                if(wuyuan!=null){
                    task.setFail_count(task.getFail_count()+1);
                    TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                    break;
                }else{
                    //情况3：失败
                    UiObject2 meiyou = mUIDevice.findObject(By.text("没抢到红包，肯定是抢的姿势不对~"));
                    if(meiyou!=null){
                        task.setFail_count(task.getFail_count()+1);
                        TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                        break;
                    }else{
                        UiObject2 yunqicha = mUIDevice.findObject(By.text("运气不佳，没抢到红包~"));
                        if(yunqicha!=null){
                            task.setFail_count(task.getFail_count()+1);
                            TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                            break;
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

        //UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
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
    public void changeIP(){
        try {
            Thread.sleep(5000);
            Intent myIntent1 = mContext.getPackageManager().getLaunchIntentForPackage("com.photon.hybrid");  //启动app
            mContext.startActivity(myIntent1);
            mUIDevice.waitForWindowUpdate("com.photon.hybrid", 5 * 2000);
            Thread.sleep(5000);
            if(vpn_quit_count<0){
                mUIDevice.pressBack();
                Thread.sleep(500);
                UiObject2 quit = mUIDevice.findObject(By.text("确认"));
                if(quit!=null){
                    quit.click();
                }
                Thread.sleep(500);
                mUIDevice.pressBack();
                Thread.sleep(500);
                UiObject2 quit1 = mUIDevice.findObject(By.text("确认"));
                if(quit1!=null){
                    quit1.click();
                }
                vpn_quit_count++;
                Thread.sleep(1500);

                mContext.startActivity(myIntent1);
                mUIDevice.waitForWindowUpdate("com.deruhai.guangzi.root", 5 * 2000);
                Thread.sleep(5000);
                UiObject2 login1 = mUIDevice.findObject(By.text("立即登录"));
                if(login1!=null){
                    login1.click();
                    Thread.sleep(5000);
                }
            }else{
                UiObject2 login = mUIDevice.findObject(By.text("立即登录"));
                if(login!=null){
                    login.click();
                    Thread.sleep(5000);
                }
                UiObject2 conn = mUIDevice.findObject(By.res("com.photon.hybrid:id/apv_switch"));
                if(conn!=null){
                    conn.click();
                }
            }
            Thread.sleep(5000);
            reboot();
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
