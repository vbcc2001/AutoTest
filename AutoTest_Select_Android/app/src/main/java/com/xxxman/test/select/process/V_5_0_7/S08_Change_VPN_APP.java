package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import com.xxxman.test.select.Constant;

import java.util.List;

/**
 * 切换飞行模式
 * Created by tuzi on 2017/10/22.
 */

public class S08_Change_VPN_APP {

    private static final String TAG = S08_Change_VPN_APP.class.getName();

    public static void start() throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(Constant.APP_NAME, 5 * 2000);
        UiObject2 set = mUIDevice.findObject(By.text("VPN"));
        if(set!=null){
            set.click();
        }
        try {
            Thread.sleep(1000);
            List<UiObject2> check2  = mUIDevice.findObjects(By.res("android:id/checkbox"));;
            if(check2.get(0).isChecked()){
                check2.get(0).click();
                Thread.sleep(4000);
            }
            Thread.sleep(1000);
            check2.get(0).click();
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
