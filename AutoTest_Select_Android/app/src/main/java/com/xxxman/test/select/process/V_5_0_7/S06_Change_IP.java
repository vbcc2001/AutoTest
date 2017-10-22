package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import com.xxxman.test.select.Constant;

/**
 * 换IP
 * Created by tuzi on 2017/10/22.
 */

public class S06_Change_IP {

    private static final String TAG = S06_Change_IP.class.getName();

    public static void start() throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();
        try {
            String ip_app = "com.photon.hybrid";
            //String ip_app = "com.deruhai.guangzi.root";
            Thread.sleep(5000);
            Intent myIntent1 = mContext.getPackageManager().getLaunchIntentForPackage(ip_app);  //启动app
            mContext.startActivity(myIntent1);
            mUIDevice.waitForWindowUpdate(ip_app, 5 * 2000);
            Thread.sleep(5000);
            UiObject2 login = mUIDevice.findObject(By.text("立即登录"));
            if(login!=null){
                login.click();
                Thread.sleep(5000);
            }
            UiObject2 conn = mUIDevice.findObject(By.res(ip_app+":id/apv_switch"));
            if(conn!=null){
                conn.click();
            }
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                S00_App_Reboot.start();
                Thread.sleep(5000);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
