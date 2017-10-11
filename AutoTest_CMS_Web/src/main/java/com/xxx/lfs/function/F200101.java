package com.xxx.lfs.function;

import com.xxx.web.function.DataRow;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


/** 查询红包  */
public class F200101 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		List<DataRow> list = query();
		response.setList(list);
		return response;
	}
	private List<DataRow> query() throws Exception {

		Timestamp ts = new Timestamp(System.currentTimeMillis()-1000*60*5*12*24);

		Object arg[] = new Object[1];
		arg[0]=ts;
		String	sql ="SELECT * FROM t_hongbao_info where update_time>? order by id desc ";
		List<DataRow> list = this.getNewJdbcTemplate().query(sql,arg);
		return list;
	}
	public static void main(String[] args) throws Exception {
		new DBConfigure().loadConfig();
		F200101 f = new F200101();
		List<DataRow> list =  f.query();
		for(DataRow dataRow:list){
			System.out.println(dataRow);
		}
	}
}
