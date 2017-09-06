package com.xxx.lfs.function;

import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


/** 导出标签  */
public class F100020 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		List<DataRow> list = query();
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
		dataRow.set("file_name","手机标签");
		response.getList().add(dataRow);
		return response;
	}
	private List<DataRow> query() throws Exception {

		String sql ="SELECT register,phone,tag,update_time FROM t_register order by tag ";
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
		F100020 f = new F100020();
		List<DataRow> list =  f.query();
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
