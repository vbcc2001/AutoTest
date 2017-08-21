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

		response.setList(list);
		return response;
	}

	private List<DataRow> query(String type) throws Exception {

		String sql ="";
		if("sun".equals(type)){
			sql ="SELECT phone,(select tag from t_register where phone=t.phone) as tag ," +
					"(select sum(sun) FROM t_count where phone=t.phone) as sun, " +
					"count(accout) count FROM t_accout t where type=? group by t.phone  order by t.phone ";
		}else{
			sql ="SELECT phone,(select tag from t_register where phone=t.phone) as tag ," +
					"(select sum(dou) FROM t_count where phone=t.phone) as dou, " +
					"count(accout) count FROM t_accout t where type=? group by t.phone  order by t.phone ";
		}

		List<DataRow> list = this.getNewJdbcTemplate().query(sql,new String[]{type});
		int i =1;
		int sum_accout = 0;
		int sum_sun = 0;
		int sum_dou = 0;
		for(DataRow dataRow:list){
			sum_accout += dataRow.getInt("count");
			if("sun".equals(type)){
				if(dataRow.getInt("sun")!=null){
					sum_sun += dataRow.getInt("sun");
				}
			}else {
				if(dataRow.getInt("dou")!=null){
					sum_dou += dataRow.getInt("dou");
				}
			}
			dataRow.set("number",i++);
			dataRow.set("id",dataRow.getString("phone"));

		}
		DataRow sum = new DataRow();
		sum.set("number",i++);
		sum.set("id","sum");
		sum.set("phone","");
		sum.set("tag","汇总");
		sum.set("count",sum_accout);
		sum.set("sun",sum_sun);
		sum.set("dou",sum_dou);
		list.add(sum);
		return list;
	}
	public static void main(String arg[] ) throws Exception{

		new DBConfigure().loadConfig();
		F100011 f = new F100011();
		List<DataRow> list =  f.query("hongbao");
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
		List<DataRow> list1 =  f.query("sun");
		for(DataRow dataRow:list1){
			System.out.println(dataRow);
		}
	}
}
