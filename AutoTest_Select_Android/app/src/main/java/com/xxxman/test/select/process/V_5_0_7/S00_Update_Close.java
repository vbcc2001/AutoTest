package com.xxxman.test.select.process.V_5_0_7;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;


/**
 * 关广告
 * Created by tuzi on 2017/10/22.
 */

public class S00_Update_Close {

    private static final String TAG = S00_Update_Close.class.getName();

    public static boolean start() {
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject2 update = mUIDevice.findObject(By.res("com.huajiao:id/tn_close"));
        if(update!=null){
            Log.d(TAG,"有升级");
            update.click();
            return true;
        }else {
            return false;
        }
    }
}
