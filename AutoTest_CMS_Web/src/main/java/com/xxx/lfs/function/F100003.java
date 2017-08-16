package com.xxx.lfs.function;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.http.listener.AWSConfig;
import com.xxx.web.jdbc.DBConfigure;

import java.lang.reflect.Type;
import java.util.Map;

/** 添加  */
public class F100003 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String id = requestParameter.getContent().get("id");
		del(id);
		return response;
	}
	private int del(String id) throws Exception {
		Object arg[] = new Object[1];
		arg[0]=Integer.valueOf(id);
		String sql="delete from t_register where id=? ";
		return getNewJdbcTemplate().update(sql,arg);
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100003 f = new F100003();
		int i= f.del("6");
		System.out.print(i);
	}
}
