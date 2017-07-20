package com.xxx.lfs.function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by tuzi on 2017/7/15.
 */

public class SNUtil {

    private static final String TAG = SNUtil.class.getName();

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


