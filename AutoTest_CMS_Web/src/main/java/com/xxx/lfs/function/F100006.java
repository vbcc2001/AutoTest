package com.xxx.lfs.function;


import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;


import java.text.SimpleDateFormat;
import java.util.List;

import java.sql.Timestamp;
import java.util.TimeZone;

/** 查询阳光、豆详情  */
public class F100006 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String phone = requestParameter.getContent().get("phone");
		List<DataRow> list = query(phone);
		response.setList(list);
		return response;
	}

	private List<DataRow> query(String phone) throws Exception {

		String sql ="SELECT *,(select tag from t_register where phone=t.phone) as tag  FROM t_count t where phone = ? ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql,new String[]{phone});
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		for(DataRow dataRow:list){
			Timestamp sunDate = (Timestamp) dataRow.get("sun_update_time");
			Timestamp douDate = (Timestamp) dataRow.get("dou_update_time");
			if(sunDate!=null){
				String sunDateString = df.format(sunDate.getTime()+1000*60*60*8);
				dataRow.set("sun_update_time",sunDateString);
			}
			if(douDate!=null){
				String douDateString = df.format(douDate.getTime()+1000*60*60*8);
				dataRow.set("dou_update_time",douDateString);
			}
		}
		return list;
	}
	public static void main(String arg[] ) throws Exception{

		new DBConfigure().loadConfig();
		F100006 f = new F100006();
		List<DataRow> list =  f.query("c4c8ba9f4fd2");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
