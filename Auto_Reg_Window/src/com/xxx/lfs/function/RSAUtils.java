package com.xxx.lfs.function; /**
 * Created by tuzi on 2017/7/15.
 */


import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author changcsw 2015-11-11
 */
public class RSAUtils {

    private static final String TAG = RSAUtils.class.getSimpleName();
    private static RSAPublicKey publicKey = null;
    private static RSAPrivateKey privateKey = null;
    private static Cipher cipher = null;
    private static Logger logger = Logger.getLogger(RSAUtils.class);

    /**************************** RSA 公钥加密解密**************************************/
    /**
     * 从字符串中加载公钥,从服务端获取
     */
    public static void loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.getDecoder().decode(pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            //logger.debug(TAG, "publicKey=" + publicKey.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 公钥加密过程
     */
    public static String encryptWithRSA(String plainData) throws Exception {
        String TRANSFORMATION = "RSA/None/NoPadding";
        // 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
        //cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        //cipher = Cipher.getInstance("RSA/NONE/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] buffer = plainData.getBytes("utf-8");
        byte[] output = cipher.doFinal(buffer);
        //logger.debug(TAG, "加密后长度=" + output.length);
        return Base64.getEncoder().encodeToString(output);
    }
}