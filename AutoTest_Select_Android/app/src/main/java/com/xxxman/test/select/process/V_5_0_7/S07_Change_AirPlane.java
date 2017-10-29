package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xxxman.test.select.Constant;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.SQLUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切换飞行模式
 * Created by tuzi on 2017/10/22.
 */

public class S07_Change_AirPlane {

    private static final String TAG = S07_Change_AirPlane.class.getName();

    public static void start() throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mUIDevice.waitForWindowUpdate(Constant.APP_NAME, 5 * 2000);
        UiObject2 set = mUIDevice.findObject(By.text("更多连接方式"));
        if(set!=null){
            set.click();
        }
        try {
            Thread.sleep(1000);
            UiObject2 fenxin = mUIDevice.findObject(By.text("飞行模式"));
            List<UiObject2> check2  = mUIDevice.findObjects(By.res("android:id/checkbox"));;
            if(!check2.get(0).isChecked()){
                check2.get(0).click();
            }
            Thread.sleep(1000);
            check2.get(0).click();
            Thread.sleep(10000);
            Connection my = new Connection();
            Gson gson = new GsonBuilder().serializeNulls().create();
            //更新到服务器
            Map<String, String> parms = new HashMap<>();
            String rs = my.getContextByHttp("http://pv.sohu.com/cityjson", parms);
            Log.d(TAG, "http请求结果" + rs);
            FileUtil.write("task_"+ SQLUtil.getDayString()+".txt",rs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
