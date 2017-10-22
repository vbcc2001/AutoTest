package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import com.xxxman.test.select.Constant;

/**
 * 重启
 * Created by tuzi on 2017/10/22.
 */

public class S00_App_Reboot {

    public static void start() {
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(Constant.APP_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(Constant.APP_NAME, 5 * 2000);
    }
}
