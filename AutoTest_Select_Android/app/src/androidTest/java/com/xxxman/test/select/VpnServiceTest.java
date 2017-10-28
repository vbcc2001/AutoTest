package com.xxxman.test.select;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xxxman.test.select.process.V_5_0_7.S09_Start_Vpn_Service;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.FileUtil;

import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tuzi on 2017/10/22.
 */

@RunWith(AndroidJUnit4.class)
public class VpnServiceTest extends S09_Start_Vpn_Service {

    private static final String TAG = VpnServiceTest.class.getSimpleName();

    @org.junit.Test
    public void test() {
        try {
            S09_Start_Vpn_Service.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
