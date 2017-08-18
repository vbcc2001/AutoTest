package com.xxx.lfs.function;


import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/** 查询账号明细  */
public class F100010 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String phone = requestParameter.getContent().get("phone");
		String type = requestParameter.getContent().get("type");
		List<DataRow> list = query(type,phone);
		int i =1;
//		for(DataRow dataRow:list){
//
//			dataRow.set("number",i++);
//			dataRow.set("id",dataRow.getString("phone"));
//
//		}
		response.setList(list);
		return response;
	}

	private List<DataRow> query(String type,String phone) throws Exception {

		String sql ="SELECT *,(select tag from t_register where phone=t.phone) as tag from t_accout t where type = ? and phone=? order by number" ;
		List<DataRow> list = this.getNewJdbcTemplate().query(sql,new String[]{type,phone});
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		for(DataRow dataRow:list){
			Timestamp date = (Timestamp) dataRow.get("update_time");
			if(date!=null){
				String dateString = df.format(date.getTime()+1000*60*60*8);
				dataRow.set("update_time",dateString);
			}
		}
		return list;
	}
	public static void main(String arg[] ) throws Exception{

		new DBConfigure().loadConfig();
		F100010 f = new F100010();
		List<DataRow> list =  f.query("sun","c4c8ba9f4fd2");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
		List<DataRow> list1 =  f.query("hongbao","c4c8ba9f4fd2");
		for(DataRow dataRow:list1){
			System.out.println(dataRow);
		}
	}
}
