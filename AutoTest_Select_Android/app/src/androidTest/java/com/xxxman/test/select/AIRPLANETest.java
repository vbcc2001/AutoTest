package com.xxxman.test.select;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.xxxman.test.select.process.V_5_0_7.M01_QiangHB;

import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.R.attr.logo;
import static android.R.attr.start;
import static android.provider.Settings.System.AIRPLANE_MODE_ON;
/**
 * Created by tuzi on 2017/10/22.
 */


@RunWith(AndroidJUnit4.class)
public class AIRPLANETest {

    @org.junit.Test
    public void test() {
        Context mContext = InstrumentationRegistry.getContext();

        int i = Settings.System.getInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        Log.d("test","---"+i+"***");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,1);
        int j = Settings.System.getInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        Log.d("test","---"+j+"***");
    }

}
