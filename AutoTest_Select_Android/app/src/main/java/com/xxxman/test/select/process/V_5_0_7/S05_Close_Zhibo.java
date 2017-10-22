package com.xxxman.test.select.process.V_5_0_7;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.xxxman.test.select.Constant;

/**
 * 关直播
 * Created by tuzi on 2017/10/22.
 */

public class S05_Close_Zhibo {

    private static final String TAG = S05_Close_Zhibo.class.getName();

    public static void start() throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        boolean is4X = Constant.IS_4X();
        if(is4X){
            mUIDevice.click(990,1843);
            mUIDevice.click(990,1843);
        }else{
            mUIDevice.click(660,1228);
            mUIDevice.click(660,1228);
        }
        mUIDevice.pressBack();
        mUIDevice.pressBack();
    }
}
