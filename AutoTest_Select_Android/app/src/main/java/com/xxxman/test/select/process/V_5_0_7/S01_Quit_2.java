package com.xxxman.test.select.process.V_5_0_7;

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
import com.xxxman.test.select.util.ShellUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 退出
 * Created by tuzi on 2017/10/22.
 */

public class S01_Quit_2 {

    private static final String TAG = S01_Quit_2.class.getName();

    public static void start() throws Exception{
        String command = "pm clear com.huajiao";
        ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
        Log.d(TAG, "command: " + command );
        Log.d(TAG, "run: " + rs.result );
        Log.d(TAG, "responseMsg: " + rs.responseMsg );
        Log.d(TAG, "errorMsg: "  + rs.errorMsg);
    }
}
