package com.xxx.lfs.function;

import java.lang.reflect.Type;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;

/** 添加  */
public class F100001 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String sun = requestParameter.getContent().get("sun");
		insert(sun);
		return response;
	}
	private void insert(String sun) throws Exception {

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> map = gson.fromJson(sun, type);

		initAmazonDynamoDB();
		DynamoDB dynamo = new DynamoDB(dynamoDB);
		Table table = dynamo.getTable("sun");

		Item item = new Item()
				.withPrimaryKey("编号", 123)
				.with("序号",map.get("序号"))
				.with("用户名",map.get("用户名"))
				.with("密码",map.get("密码"))
				.with("日期",map.get("日期"))
				.with("是否成功",map.get("是否成功"))
				.with("阳光数",map.get("阳光数"));
		table.putItem(item);
	}
	public static void main(String arg[] ) throws Exception{
		F100001 f = new F100001();
		f.insert("{\"序号\":3,\"用户名\":\"18926085629\",\"密码\":\"123456\",\"日期\":312,\"是否成功\":true,\"阳光数\":30}");
	}
}
