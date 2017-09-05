package com.xxx.lfs.function;

import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


/** 查询更新  */
public class F100015 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String accout = requestParameter.getContent().get("accout");
		String pwd = requestParameter.getContent().get("pwd");
		update(accout,pwd);
		return response;
	}
	private int update(String accout,String pwd ) throws Exception {

		Object arg[] = new Object[2];
		arg[0]=pwd;
		arg[1]=accout;
		String sql="update t_accout set pwd=? where accout=?";
		return getNewJdbcTemplate().update(sql,arg);
	}
	public static void main(String[] args) throws Exception {
		new DBConfigure().loadConfig();
		F100015 f = new F100015();
		f.update("15889649769","130531377");
	}
}
