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

public class S09_Start_Vpn_Service {

    private static final String TAG = S09_Start_Vpn_Service.class.getName();

    public static void start() throws Exception{
        Context mContext = InstrumentationRegistry.getContext();
        Intent mIntent = new Intent();
        mIntent.setAction("android.net.VpnService");//你定义的service的action
        mIntent.setPackage("com.xxxman.test.select");//这里你需要设置你应用的包名
        mContext.startService(mIntent);
    }
}
