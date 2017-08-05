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
		String sun = requestParameter.getContent().get("sun");
		insert(sun);
		return response;
	}
	private int insert(String sun) throws Exception {
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> para = gson.fromJson(sun, type);

		Object arg[] = new Object[5];
		arg[0]=para.get("phone");
		arg[1]=para.get("pwd");
		arg[2]=para.get("state");
		arg[3]=para.get("sun");
		arg[4]=para.get("account");
		String sql="INSERT INTO t_sun(phone,pwd,state,sun,account) VALUES (?,?,?,?,?)";
		return getNewJdbcTemplate().update(sql,arg);
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100003 f = new F100003();
		int i= f.insert("{\"phone\":\"x-001\",\"account\":\"18926085629\",\"pwd\":\"123456\",\"state\":\"1\",\"sun\":30}");
		System.out.print(i);
	}
}
