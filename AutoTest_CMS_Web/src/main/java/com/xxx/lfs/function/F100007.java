package com.xxx.lfs.function;


import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.util.ArrayList;
import java.util.List;

/** 添加  */
public class F100007 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		List<DataRow> list = query();
		List<DataRow> list1 = new ArrayList<DataRow>();
		int i =1;
		for(DataRow dataRow:list){
//			DataRow row = new DataRow();
//			row.set("id",i++);
//			List data = new ArrayList<String>();
//			data.add(dataRow.getString("phone"));
//			data.add(dataRow.getString("count"));
//			data.add(dataRow.getString("sun"));
//			data.add(dataRow.getString("dou"));
//			row.set("data",data);
//			list1.add(row);
			dataRow.set("number",i++);
			dataRow.set("id",dataRow.getString("phone"));

		}
		response.setList(list);
		return response;
	}

	private List<DataRow> query() throws Exception {

		String sql ="SELECT phone , count(phone) count,sum(sun) sun ,sum(dou) dou FROM t_count t group by t.phone order by t.phone ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql);
		return list;
	}
	public static void main(String arg[] ) throws Exception{

		new DBConfigure().loadConfig();
		F100007 f = new F100007();
		List<DataRow> list =  f.query();
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
