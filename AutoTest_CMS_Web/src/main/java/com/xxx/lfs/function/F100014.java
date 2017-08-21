package com.xxx.lfs.function;

import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

/** 删除更新  */
public class F100014 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String id = requestParameter.getContent().get("id");
		del(id);
		return response;
	}
	private int del(String id) throws Exception {
		Object arg[] = new Object[1];
		arg[0]=Integer.valueOf(id);
		String sql="delete from t_update where id=? ";
		return getNewJdbcTemplate().update(sql,arg);
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100014 f = new F100014();
		int i= f.del("6");
		System.out.print(i);
	}
}
