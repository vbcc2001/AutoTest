package com.xxxman.autotest.shell;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import static android.text.TextUtils.isEmpty;

/**
 * Created by tuzi on 2017/7/15.
 */

public class SNUtil {

    private static final String TAG = SNUtil.class.getName();

    public static String getuniqueId(Context context){

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiMac = wifi.getConnectionInfo().getMacAddress();

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //String imei = tm.getDeviceId();
        String imei = null;
        String simSerialNumber = tm.getSimSerialNumber();
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
        String SerialNumber = android.os.Build.SERIAL;
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        if(!isEmpty(wifiMac)) {
            deviceId.append("wifi");
            deviceId.append(wifiMac);
        }
        if(!isEmpty(imei)) {
            deviceId.append("imei");
            deviceId.append(imei);
        }
        if(!isEmpty(simSerialNumber)) {
            deviceId.append("simSerialNumber");
            deviceId.append(simSerialNumber);
        }
        if(!isEmpty(androidId)) {
            deviceId.append("androidId");
            deviceId.append(androidId);
        }
        if(!isEmpty(SerialNumber)) {
            deviceId.append("SerialNumber");
            deviceId.append(SerialNumber);
        }
        Log.d(TAG, deviceId.toString());

        String code  = "";
        try {
            code  = getMD5(deviceId.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.d(TAG, code);

        return code;

    }
    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(Integer.toHexString(0xff & b[i]));
            //sb.append(b[i]);
            //571aba159ebcae2fd02257dd261f16d
        }
        return sb.toString();
    }
}


