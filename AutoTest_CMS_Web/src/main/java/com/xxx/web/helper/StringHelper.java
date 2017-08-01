package com.xxx.web.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理帮助类
 * @author 门士松  20121027
 * @version 1.0
 * @since
 */
public class StringHelper{
	/**
	 * 验证是null或长度为0
	 */
	public static boolean isEmpty(String str) {
		return (str==null || str.trim().length()<1);
	}
	/**
	 * 将null转换为""
	 */
	public static String ensure(String str){
		return str==null?"":str;
	}
	/**
	 * 判断是否为数字
	 * @param str
	 */
	public static boolean isNumber(String str){
		Pattern pattern=java.util.regex.Pattern.compile("[0-9]*"); 
        Matcher match=pattern.matcher(str);
        return match.matches();
	}
	/**
	 * md5加密
	 * @param input
	 * @return md5加密后
	 */
	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}	
	
}
