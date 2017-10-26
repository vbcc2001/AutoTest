package com.xxxman.test.select;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.util.Connection;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.RSAUtils;

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
public class SettingTest {

    private static final String TAG = SettingTest.class.getSimpleName();

    @org.junit.Test
    public void test() {
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context mContext = InstrumentationRegistry.getContext();


            try {
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.android.settings");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
                mUIDevice.waitForWindowUpdate(Constant.APP_NAME, 5 * 2000);

                UiObject2 set = mUIDevice.findObject(By.text("更多连接方式"));
                if (set != null) {
                    Log.d("teest", "有广告");
                    set.click();
                }
                Thread.sleep(1000);
                for (int j = 0; j < 1000; j++) {
                    UiObject2 fenxin = mUIDevice.findObject(By.text("飞行模式"));
                    List<UiObject2> check2 = mUIDevice.findObjects(By.res("android:id/checkbox"));
                    ;
                    if (!check2.get(0).isChecked()) {
                        check2.get(0).click();
                    }
                    Thread.sleep(1000);
                    check2.get(0).click();
                    Thread.sleep(5000);

                    Connection my = new Connection();
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    //更新到服务器
                    Map<String, String> parms = new HashMap<>();
                    String rs = my.getContextByHttp("http://pv.sohu.com/cityjson", parms);
                    Log.d(TAG, "http请求结果" + rs);
                    FileUtil.write("ip.txt",rs);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Preference IpAddress", ex.toString());
        }
        return null;
    }
}
