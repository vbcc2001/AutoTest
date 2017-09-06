package com.xxx.lfs.function;

import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/** 导出豆／阳光明细  */
public class F100022 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String type = requestParameter.getContent().get("type");
		String ids = requestParameter.getContent().get("ids");
		ids = "'"+ids.replaceAll(",","','")+"'";
		List<DataRow> list = query(type,ids);
		String csv = "";
		for(DataRow dataRow: list){
			String row ="";
			for(String key: dataRow.keySet()){
				row = row+dataRow.getString(key)+",";
			}
			csv = csv +row+"\r\n";
		}
		DataRow dataRow = new DataRow();
		dataRow.set("csv",csv);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = df.format(new Date().getTime()+1000*60*60*8);
		if("sun".equals(type)){
			dataRow.set("file_name","阳光详细信息_"+dateString);
		}else{
			dataRow.set("file_name","豆详细信息_"+dateString);
		}
		response.getList().add(dataRow);
		return response;
	}
	private List<DataRow> query(String type,String ids) throws Exception {

		String sql ="";
		if("sun".equals(type)){
			sql ="SELECT number,phone,accout,pwd,(select tag from t_register where phone=t.phone) as tag," +
					"(select sun from t_count where account=t.accout) as sun," +
					"(select sun_update_time from t_count where account=t.accout) as sun_update_time" +
					" from t_accout t where type = ? and phone in("+ids+") order by phone,number" ;
		}else{
			sql ="SELECT number,phone,accout,pwd,(select tag from t_register where phone=t.phone) as tag," +
					"(select dou from t_count where account=t.accout) as dou," +
					"(select dou_update_time from t_count where account=t.accout) as dou_update_time" +
					" from t_accout t where type = ? and phone in("+ids+") order by phone,number" ;
		}
		List<DataRow> list = this.getNewJdbcTemplate().query(sql,new String[]{type});
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		for(DataRow dataRow:list){
			Timestamp date = (Timestamp) dataRow.get("update_time");
			if(date!=null){
				String dateString = df.format(date.getTime()+1000*60*60*8);
				dataRow.set("update_time",dateString);
			}
			Timestamp dou_date = (Timestamp) dataRow.get("dou_update_time");
			if(dou_date!=null){
				String dateString = df.format(dou_date.getTime()+1000*60*60*8);
				dataRow.set("dou_update_time",dateString);
			}
			Timestamp sun_date = (Timestamp) dataRow.get("sun_update_time");
			if(sun_date!=null){
				String dateString = df.format(sun_date.getTime()+1000*60*60*8);
				dataRow.set("sun_update_time",dateString);
			}
		}
		return list;
	}
	public static void main(String[] args) throws Exception {
		new DBConfigure().loadConfig();
		F100022 f = new F100022();
		List<DataRow> list =  f.query("hongbao","'11f55ec4f3f2','14d96b41deb6'");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
