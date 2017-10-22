package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 退出
 * Created by tuzi on 2017/10/22.
 */

public class S01_Quit {

    private static final String TAG = S01_Quit.class.getName();

    public static void start(Task task,boolean is_record_dou) throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject my = mUIDevice.findObject(new UiSelector().text("我的"));
        my.click();
        UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/swipeLayout"));
        home.scrollToEnd(1);
        if(is_record_dou){
            UiObject2 dou = mUIDevice.findObject(By.res("com.huajiao:id/hjd_num_tv"));
            if (dou != null){
                Log.d(TAG,"豆数为："+dou.getText()+"---------");
                int dou_sum = Integer.valueOf(dou.getText());
                if(dou_sum>0){
                    Map<String,String> map = new HashMap<>();
                    map.put("phone",S00_Get_Sn_Code.getCode());
                    map.put("account",task.getPhone());
                    map.put("state","1");
                    map.put("pwd","*");
                    map.put("dou",task.getPhone());
                    HttpResult httpResult = HttpUtil.post("F100005",map);
                }
            }
        }
        UiObject setting = mUIDevice.findObject(new UiSelector().text("设置"));
        setting.click();
        UiObject2 set = mUIDevice.findObject(By.res("android:id/content")).getChildren().get(0).getChildren().get(1);
        set.scroll(Direction.DOWN, 0.8f);
        UiObject quit = mUIDevice.findObject(new UiSelector().text("退出登录"));
        quit.click();
        UiObject quit_ok = mUIDevice.findObject(new UiSelector().text("退出"));
        quit_ok.click();
    }
}
