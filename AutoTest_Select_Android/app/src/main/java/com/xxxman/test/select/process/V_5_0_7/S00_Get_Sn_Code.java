package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

/**
 * 获取任务
 * Created by tuzi on 2017/10/22.
 */

public class S00_Get_Sn_Code {

    private static final String TAG = S00_Get_Sn_Code.class.getName();

    public static String getCode() throws InterruptedException {
        Context mContext = InstrumentationRegistry.getContext();
        SharedPreferences preferences= mContext.getSharedPreferences("sn_code", Context.MODE_PRIVATE);
        String sn_code =preferences.getString("sn_code", "xxx");
        Log.d(TAG,"注册码为："+sn_code);
        return sn_code;
    }
    public static String getCode(Context mContext) {
        SharedPreferences preferences= mContext.getSharedPreferences("sn_code", Context.MODE_PRIVATE);
        String sn_code =preferences.getString("sn_code", "xxx");
        Log.d(TAG,"注册码为："+sn_code);
        return sn_code;
    }
}
