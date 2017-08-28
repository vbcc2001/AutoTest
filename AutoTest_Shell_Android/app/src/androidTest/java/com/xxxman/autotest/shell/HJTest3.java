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
import java.util.ArrayList;
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
    int fail_count= 0 ;
    SQLUtil1 sqlUtil = new SQLUtil1();
    boolean is4X=Constant.IS_4X;
    String citys[] = new String[]{"最新","北京","上海","广州","深圳","黑龙江","吉林","辽宁"};
    int next_city = 0;
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
    public void test_test() throws  Exception{
//    UiObject2 list = mUIDevice.findObject(By.res("com.huajiao:id/hot_area_list"));
//    ist.scroll(Direction.UP, 10f);

        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
        home.scrollToEnd(1);
    }
    @Test
    public void test(){
        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
        MyConnection my  = new MyConnection();
        String url = Constant.URL;
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
            FileUtil.writehengxian(list.size(),path,"bh_fail_"+sqlUtil.dateString+".txt");
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
                count_get_hongbao = user.hongbao ;
                int hongbao_count_two = 0;
                //如果完成了3次抢红包，则要求抢6次，
                if(count_get_hongbao>=Constant.HONGBAO_COUNT_ONE){
                    hongbao_count_two = Constant.HONGBAO_COUNT - Constant.HONGBAO_COUNT_ONE;
                }
                fail_count = 0;
                login(user);
                if(Constant.IS_HSM){
                    Thread.sleep(3000);
                }
                for (int i = 0; i < 100; i++) {
                    try {
                        if(Constant.IS_HSM){
                            //selectCityHSM(i,user);
                            selectCityHSM2(user);
                        }else{
                            selectCity(i,user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UiObject2 fenghao = mUIDevice.findObject(By.text("冤枉，我要申诉"));
                        if(fenghao!=null){
                            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                            List<User> list = new ArrayList<>();
                            user.pwd = user.pwd+",账号被封";
                            list.add(user);
                            FileUtil.writeTxtFile(list,path,"bh_fail_"+sqlUtil.dateString+".txt");
                            break;
                        }
                        UiObject2 yanzhengma = mUIDevice.findObject(By.text("账号已被锁定，请通过短信验证码登录"));
                        if (yanzhengma!=null){
                            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                            List<User> list = new ArrayList<>();
                            user.pwd = user.pwd+",短信验证";
                            list.add(user);
                            FileUtil.writeTxtFile(list,path,"bh_fail_"+sqlUtil.dateString+".txt");
                            break;
                        }
                        UiObject2 yanzhengma2 = mUIDevice.findObject(By.text("获取短信验证码"));
                        if (yanzhengma2!=null){
                            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                            List<User> list = new ArrayList<>();
                            user.pwd = user.pwd+",短信验证";
                            list.add(user);
                            FileUtil.writeTxtFile(list,path,"bh_fail_"+sqlUtil.dateString+".txt");
                            break;
                        }
                        UiObject2 shiming = mUIDevice.findObject(By.text("实名认证提示"));
                        if (shiming!=null){
                            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                            List<User> list = new ArrayList<>();
                            user.pwd = user.pwd+",实名认证";
                            list.add(user);
                            FileUtil.writeTxtFile(list,path,"bh_fail_"+sqlUtil.dateString+".txt");
                            break;
                        }
                        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
                        if (login!=null){
                            break;
                        }
                        reboot();
                        if(Constant.IS_HSM){
                            Thread.sleep(5000);
                        }
                    }
                    if (count_get_hongbao>=(Constant.HONGBAO_COUNT_ONE+hongbao_count_two)){
                        break;
                    }
                    if (fail_count>=3){
                        break;
                    }
                    //提醒
                    Intent intent = new Intent();
                    intent.setAction("com.xxxman.autotest.shell.MyBroadCastReceiver");
                    intent.putExtra("name", "当前为第"+user.number+"用户,已刷新第"+(i+1)+"次，已抢红包"+count_get_hongbao+"个");
                    mContext.sendBroadcast(intent);
                }
                quit(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
        }
    }
    public void selectCity(int i,User user) throws Exception{
        if (i%4!=3){
            find_money(user,"最新");
        }
        if (i%4==3){
            if((i/4)%4==0){
                UiObject2 city = mUIDevice.findObject(By.text("深圳"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("北京"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("上海"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("广州"));
                    }
                    Log.d(TAG,"切换深圳");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    if("hd".equals(Constant.TAG)){
                        find_money(user,"深圳");
                    }else{
                        find_money(user,"深圳 (当前定位地区)");
                    }
                }else{
                    find_money(user,"深圳");
                }
            }
            if((i/4)%4==1){
                UiObject2 city = mUIDevice.findObject(By.text("北京"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("深圳"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("上海"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("广州"));
                    }
                    Log.d(TAG,"切换北京");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    find_money(user,"北京");
                }else{
                    find_money(user,"北京");
                }
            }
            if((i/4)%4==2){
                UiObject2 city = mUIDevice.findObject(By.text("上海"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("深圳"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("北京"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("广州"));
                    }
                    Log.d(TAG,"切换上海");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    find_money(user,"上海");
                }else{
                    find_money(user,"上海");
                }
            }
            if((i/4)%4==3){
                UiObject2 city = mUIDevice.findObject(By.text("广州"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("深圳"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("北京"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("上海"));
                    }
                    Log.d(TAG,"切换广州");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    find_money(user,"广州");
                }else{
                    find_money(user,"广州");
                }
            }
        }

    }
    public void selectCityHSM(int i,User user) throws Exception{
        if (i%2==0){
            find_money(user,"最新");
        }else{
            if((i/2)%4==0){
                UiObject2 city = mUIDevice.findObject(By.text("北京"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("黑龙江"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("吉林"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("辽宁"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("深圳"));
                    }
                    Log.d(TAG,"切换北京");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    city = mUIDevice.findObject(By.text("北京"));
                    if (city==null){
                        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                        home.scrollToBeginning(1);
                        home.scrollToBeginning(1);
                    }
                    find_money(user,"北京");
                }else{
                    find_money(user,"北京");
                }
            }
            if((i/2)%4==1){
                UiObject2 city = mUIDevice.findObject(By.text("黑龙江"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("吉林"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("辽宁"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("北京"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("深圳"));
                    }
                    Log.d(TAG,"切换黑龙江");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    city = mUIDevice.findObject(By.text("黑龙江"));
                    if (city==null){
                        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                        home.scrollToBeginning(1);
                        home.scrollToEnd(1);
                    }
                    find_money(user,"黑龙江");
                }else{
                    find_money(user,"黑龙江");
                }
            }
            if((i/2)%4==2){
                UiObject2 city = mUIDevice.findObject(By.text("吉林"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("辽宁"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("北京"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("黑龙江"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("深圳"));
                    }
                    Log.d(TAG,"切换吉林");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    city = mUIDevice.findObject(By.text("吉林"));
                    if (city==null){
                        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                        home.scrollToBeginning(1);
                        home.scrollToEnd(1);
                    }
                    find_money(user,"吉林");
                }else{
                    find_money(user,"吉林");
                }
            }
            if((i/2)%4==3){
                UiObject2 city = mUIDevice.findObject(By.text("辽宁"));
                if (city==null){
                    city = mUIDevice.findObject(By.text("北京"));
                    if (city==null){
                        city = mUIDevice.findObject(By.text("黑龙江"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("吉林"));
                    }
                    if (city==null){
                        city = mUIDevice.findObject(By.text("深圳"));
                    }
                    Log.d(TAG,"切换辽宁");
                    city.click();
                    Thread.sleep(1000);
                    city.click();
                    city = mUIDevice.findObject(By.text("辽宁"));
                    if (city==null){
                        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                        home.scrollToBeginning(1);
                        home.scrollToEnd(1);
                    }
                    find_money(user,"辽宁");
                }else{
                    find_money(user,"辽宁");
                }
            }
        }

    }
    public void selectCityHSM2(User user) throws Exception{
        if(next_city>=citys.length){
            next_city = 0;
        }
        next_city++;
        selectCityHSM3(user,citys[next_city-1]);
    }
    public void selectCityHSM3(User user,String city) throws Exception{
        Log.d(TAG,"----------进行城市--------："+city);
        if("最新".equals(city)){
            find_money(user,city);
        }else{
            UiObject2 city_ui = mUIDevice.findObject(By.text(city));

            if (city_ui==null){
                for(int i=1;i<citys.length;i++){
                    Log.d(TAG,"-------查看城市----------"+citys[i]);
                    city_ui = mUIDevice.findObject(By.text(citys[i]));
                    if (city_ui!=null){
                        if(!city_ui.isSelected()){
                            city_ui.click();
                        }
                        Thread.sleep(1000);
                        city_ui.click();
                        if(Constant.LO_CITY.equals(city)){
                            city =city +" (当前定位地区)";
                        }
                        city_ui = mUIDevice.findObject(By.text(city));
                        if (city_ui==null){
                            UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                            home.scrollToBeginning(1);
                            city_ui = mUIDevice.findObject(By.text(city));
                            if (city_ui==null){
                                home.scrollToEnd(1);
                            }
                            break;
                        }else{
                            break;
                        }
                    }
                }
                find_money(user,city);
            }else{
                find_money(user,city);
            }
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
//        UiObject2 new_list = mUIDevice.findObject(By.text(menu));
//        if (new_list==null){
//            new_list = mUIDevice.findObject(By.text(menu+" (当前定位地区)"));
//        }
        new_list.click();
        int c = 18;
        if(!menu.equals("最新")){
            c = 12;
        }
        for(int i =0 ;i < c ;i++){
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
                Thread.sleep(1500);
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
                        kaihongbao.click();
                        Thread.sleep(1000);
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
                            fail_count++;
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
                                fail_count++;
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
                                    fail_count++;
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
                                                //if(j>2){
                                                    if (is4X) {
                                                        mUIDevice.click(990, 1843);
                                                        mUIDevice.click(990, 1843);
                                                    } else {
                                                        mUIDevice.click(660, 1228);
                                                        mUIDevice.click(660, 1228);
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

