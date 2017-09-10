package com.xxx.lfs.function;

import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.math.BigInteger;
import java.security.MessageDigest;

/** 添加或修改标签 */
public class F100031 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String accout = requestParameter.getContent().get("accout");
		String pwd = requestParameter.getContent().get("pwd");
		insert(accout,pwd);
		return response;
	}
	private int insert(String accout,String pwd) throws Exception {

		pwd = getMD5(pwd);
		Object arg[] = new Object[2];
		arg[0]=accout;
		arg[1]=pwd;
		String sql="INSERT INTO t_user(accout,pwd,state,update_time) VALUES (?,?,'1',now())";
		return getNewJdbcTemplate().update(sql,arg);
	}
	/**
	 * 对字符串md5加密
	 *
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) throws Exception {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			throw new Exception("MD5加密出现错误");
		}
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100031 f = new F100031();
		f.insert("123","123");
	}
}
