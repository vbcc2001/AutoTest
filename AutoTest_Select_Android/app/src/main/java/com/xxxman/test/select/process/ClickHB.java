package com.xxxman.test.select.process;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import com.xxxman.test.select.object.HttpRequest;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.HttpUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ClickHB {
    private static final String TAG = SelectHB.class.getName();
    String APP = "com.huajiao";
    int log_count = 0;
    UiDevice mUIDevice = null;
    Context mContext = null;
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
            for (int i = 0; i < 9999; i++) {
                HttpResult httpResult = HttpUtil.post("F100005");
                if("".equals(httpResult.getErrorNo())) {

                }else{
                    Log.e(TAG,"获取用户信息失败："+httpResult.getErrorInfo());
                    Thread.sleep(5000);
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
}
