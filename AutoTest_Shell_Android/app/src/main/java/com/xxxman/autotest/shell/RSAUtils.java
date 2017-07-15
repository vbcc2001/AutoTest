package com.xxxman.autotest.shell;

/**
 * Created by tuzi on 2017/7/15.
 */

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;

/**
 * @author changcsw 2015-11-11
 *
 */
public class RSAUtils {

    private static final String TAG = RSAUtils.class.getSimpleName();
    private static RSAPublicKey publicKey = null;
    private static RSAPrivateKey privateKey = null;
    private static Cipher cipher = null;
    //随机生成密码对
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            KeyPair kp = kpg.genKeyPair();
            String priKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
            String pubKey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);
            Log.d(TAG,"---Private Key---\r\n"+kp.getPrivate().toString());
            Log.d(TAG,"Base64String="+priKey);
            Log.d(TAG,"---Public Key---\r\n"+kp.getPublic().toString());
            Log.d(TAG,"Base64String="+pubKey);
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
    public static void loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            Log.d(TAG,"publicKey="+publicKey.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] buffer = plainData.getBytes("utf-8");
        byte[] output = cipher.doFinal(buffer);
        Log.d(TAG,"加密后长度="+output.length);
        return Base64.encodeToString(output, Base64.DEFAULT);
    }
    public static String decryptWithRSA(String encryedData) throws Exception{
            // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] buffer = encryedData.getBytes("ISO-8859-1");
            byte[] output = cipher.doFinal(buffer);
            return new String(output);
    }

    public void a(){
        Signature Signer =Signature.getInstance("SHA1withRSA");

        Signer.initSign(MyKey, new SecureRandom()); //Where do you get the key?

        byte []Message = MyMessage(); //Initialize somehow

        Signer.update(Message, 0, Message.length);

        byte [] Signature = Sign.sign();
    }

    /**
     * 公钥解密过程
     */
//    public static String decryptWithRSA(String encryedData) throws Exception {
//
//        // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
//        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, publicKey);
//        byte[] output = cipher.doFinal(Base64.decode(encryedData, Base64.DEFAULT));
//        return new String(output);
//    }
    /**************************** RSA 公钥加密解密**************************************/
}