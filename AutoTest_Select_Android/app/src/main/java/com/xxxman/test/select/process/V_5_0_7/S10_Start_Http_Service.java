package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

/**
 * 切换飞行模式
 * Created by tuzi on 2017/10/22.
 */

public class S10_Start_Http_Service {

    private static final String TAG = S10_Start_Http_Service.class.getName();

    public static void start() throws Exception{
        Context mContext = InstrumentationRegistry.getContext();
        Intent mIntent = new Intent();
        mIntent.setAction("android.net.MyHttpService");//你定义的service的action
        mIntent.setPackage("com.xxxman.test.select");//这里你需要设置你应用的包名
        mContext.startService(mIntent);
    }
}
