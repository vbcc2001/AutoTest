package com.xxx.lfs.function2;

import com.xxx.lfs.function.BaseFunction;
import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


/** 查询验证码  */
public class F200001 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String register = requestParameter.getContent().get("sn");
		List<DataRow> list = query(register);
		response.setList(list);
		return response;
	}
	private List<DataRow> query(String register) throws Exception {

		String sql = "";
		List<DataRow> list;
		if(register==null){
			sql ="SELECT * FROM t_code order by update_time desc ";
			list = this.getNewJdbcTemplate().query(sql);
		}else {
			sql ="SELECT * FROM t_code where register=? order by update_time desc ";
			list = this.getNewJdbcTemplate().query(sql,new String[]{register});
		}

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
		F200001 f = new F200001();
		List<DataRow> list =  f.query("544e5f7be0f3761502f51b6486ba776");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
