package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import com.xxxman.test.select.object.Task;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by tuzi on 2017/11/1.
 */

public class M02_Register {

    private static final String TAG = M02_Register.class.getName();
    UiDevice mUIDevice = null;
    Context mContext = null;
    @Test
    public void test() {
        try {
            //启动APP
            S00_App_Reboot.start();
            Thread.sleep(3000);

            for(int i = 0 ;i<100;i++){
                try {
                    S01_Register.start();
                    S01_Quit_2.start();
                    S00_App_Reboot.start();
                }catch (Exception e){
                    e.printStackTrace();
                    S00_App_Reboot.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Before
    public void before() throws RemoteException {
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("close_ad", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                // just press back
                Log.d(TAG,":进入Watcher-close_ad");
                boolean flag = false;
                try {
                    flag = S00_AD_Close.start();
                    if(!flag){
                        flag = S00_Update_Close.start();
                    }
                } catch (Exception e) {
                    Log.d(TAG,":关闭广告失败");
                    e.printStackTrace();
                }
                return flag;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
}
