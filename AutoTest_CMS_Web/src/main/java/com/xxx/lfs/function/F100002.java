package com.xxx.lfs.function;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;


/** 查询标签  */
public class F100002 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		List<DataRow> list = query();
		response.setList(list);
		return response;
	}
	private List<DataRow> query() throws Exception {

		String sql ="SELECT * FROM t_register order by tag ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		int i=1;
		for(DataRow dataRow:list){
			Timestamp date = (Timestamp) dataRow.get("update_time");
			if(date!=null){
				String dateString = df.format(date.getTime()+1000*60*60*8);
				dataRow.set("update_time",dateString);
			}
			dataRow.set("number",i++);
		}
		return list;
	}
	public static void main(String[] args) throws Exception {
		new DBConfigure().loadConfig();
		F100002 f = new F100002();
		List<DataRow> list =  f.query();
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
