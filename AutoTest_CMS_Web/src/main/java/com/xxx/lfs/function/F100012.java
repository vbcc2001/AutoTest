package com.xxx.lfs.function;

import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


/** 查询更新  */
public class F100012 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		List<DataRow> list = query();
		response.setList(list);
		return response;
	}
	private List<DataRow> query() throws Exception {

		String sql ="SELECT * FROM t_update order by update_time desc  ";
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
			dataRow.set("link",dataRow.getString("url")+"^"+dataRow.getString("url"));
		}
		return list;
	}
	public static void main(String[] args) throws Exception {
		new DBConfigure().loadConfig();
		F100012 f = new F100012();
		List<DataRow> list =  f.query();
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
