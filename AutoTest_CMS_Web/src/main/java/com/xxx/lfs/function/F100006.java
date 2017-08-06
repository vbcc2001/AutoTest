package com.xxx.lfs.function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;
import com.xxx.web.jdbc.DataRow;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** 添加  */
public class F100006 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String sun = requestParameter.getContent().get("count");
		isExist();
		return response;
	}

	private List<DataRow> isExist() throws Exception {

		String sql ="SELECT * FROM t_count ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql);
		return list;
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100006 f = new F100006();
		//f.isExist();
		//int i= f.insert("{\"phone\":\"x-001\",\"account\":\"18926085628\",\"pwd\":\"123456\",\"state\":\"1\",\"dou\":30}");
		List<DataRow> list1 =  f.isExist();
		for(DataRow dataRow:list1){
			System.out.println(dataRow);
		}
	}
}
