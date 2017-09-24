package com.xxxman.autotest.shell;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class HJTest4B extends HJTest3{

    String TAG = "HJTest4";
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
            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
            List<User> list = FileUtil.ReadTxtFile(path+"/bh_NumberList.txt");

            if(list.size()>0) {
                for(User user:list){
                    try{
                        login(user);
                        quit2(user);
                    }catch (Exception e){
                        e.printStackTrace();
                        reboot();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
        }
    }
    //退出流程
    public void quit2(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        //UiObject my_page = mUIDevice.findObject(new UiSelector().text("我的主页"));
        //my_page.click();
        Thread.sleep(1000);
        UiObject2 acc = mUIDevice.findObject(By.res("com.huajiao:id/uid_tv"));
        if(acc!=null){
            user.pwd = acc.getText();
            MyConnection my1  = new MyConnection();
            String url = Constant.URL;
            Map<String,String> parms = new HashMap<>();
            String context = "{\"function\":\"F100015\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"accout\":\""+user.phone+"\",\"pwd\":\"" + user.pwd + "\"}}";
            parms.put("jsonContent",context);
            Log.d(TAG,"jsonContent:"+context);
            String rs = my1.getContextByHttp(url,parms);
            Log.d(TAG,"http请求结果"+rs);
        }
        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.scrollToEnd(1);
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
}

