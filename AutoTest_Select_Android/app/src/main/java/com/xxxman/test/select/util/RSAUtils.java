package com.xxxman.test.select.util;

/**
 * Created by tuzi on 2017/7/15.
 */

import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * @author changcsw 2015-11-11
 *
 */
public class RSAUtils {

    private static final String TAG = RSAUtils.class.getSimpleName();
    private static RSAPublicKey publicKey = null;
    private static RSAPrivateKey privateKey = null;
    private static Cipher cipher = null;
    private static String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
            "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
            "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
            "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
            "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
            "ioZotQIDAQAB";
    //随机生成密码对
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
            //kpg.initialize(256);
            kpg.initialize(keyLength);
            KeyPair kp = kpg.genKeyPair();
            String priKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
            String pubKey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);
            Log.d(TAG,"---Private Key---\r\n"+kp.getPrivate().toString());
            Log.d(TAG,"Base64String="+priKey);
            Log.d(TAG,"---Public Key---\r\n"+kp.getPublic().toString());
            Log.d(TAG,"Base64String="+pubKey);
            publicKey =(RSAPublicKey) kp.getPublic();
            privateKey =(RSAPrivateKey) kp.getPrivate();
            return kp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //随机生成密码对 2048位
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(2048);
    }

    /**************************** RSA 公钥加密解密**************************************/
    /** 从字符串中加载公钥,从服务端获取 */
    public static RSAPublicKey loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            Log.d(TAG,"publicKey="+publicKey.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }
    /** 从字符串中加载公钥,从本地端获取固定Key */
    public static RSAPublicKey loadLocalPublicKey() {
        return loadPublicKey(pubkey);
    }
    /** 从字符串中加载私钥, */
    public static void loadPrivateKey(String priKey) {
        try {
            byte[] buffer = Base64.decode(priKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 公钥加密过程 */
    public static String encryptWithRSA(String plainData) throws Exception {

        // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
        //cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher = Cipher.getInstance("RSA/NONE/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, loadLocalPublicKey());
        byte[] buffer = plainData.getBytes("utf-8");
        byte[] output = cipher.doFinal(buffer);
        Log.d(TAG,"加密后长度="+output.length);
        return Base64.encodeToString(output, Base64.DEFAULT);
    }
    /** 公钥加密过程 */
    public static String encryptWithRSA(String plainData,RSAPublicKey publicKey) throws Exception {

        // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
        //cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher = Cipher.getInstance("RSA/NONE/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] buffer = plainData.getBytes("utf-8");
        byte[] output = cipher.doFinal(buffer);
        Log.d(TAG,"加密后长度="+output.length);
        return Base64.encodeToString(output, Base64.DEFAULT);
    }
    //私钥解密
    public static String decryptWithRSA(String encryedData) throws Exception{
            // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
            //cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher = Cipher.getInstance("RSA/NONE/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] buffer = Base64.decode(encryedData, Base64.DEFAULT);
            byte[] output = cipher.doFinal(buffer);
            return new String(output);
    }
    //签名
    public static String sign(String data) throws Exception {

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("utf-8"));
        return Base64.encodeToString(signature.sign(),Base64.DEFAULT);
    }
    //验证签名
    public static boolean verify(String data,String sign) throws Exception {

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes("utf-8"));
        // 验证签名是否正常
        return signature.verify(Base64.decode(sign,Base64.DEFAULT));
    }
}