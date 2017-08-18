package com.xxx.lfs.function;


import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.util.List;

/** 查询阳光、豆的汇总  */
public class F100011 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String type = requestParameter.getContent().get("type");
		List<DataRow> list = query(type);
		int i =1;
		for(DataRow dataRow:list){

			dataRow.set("number",i++);
			dataRow.set("id",dataRow.getString("phone"));

		}
		response.setList(list);
		return response;
	}

	private List<DataRow> query(String type) throws Exception {

		String sql ="SELECT phone,(select tag from t_register where phone=t.phone) as tag , " +
				"count(accout) count FROM t_accout t where type=? group by t.phone  order by t.phone ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql,new String[]{type});
		return list;
	}
	public static void main(String arg[] ) throws Exception{

		new DBConfigure().loadConfig();
		F100011 f = new F100011();
		List<DataRow> list =  f.query("sun");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
