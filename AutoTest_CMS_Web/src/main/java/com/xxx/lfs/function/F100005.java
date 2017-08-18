package com.xxx.lfs.function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.lang.reflect.Type;
import java.util.Map;

/** 添加豆信息  */
public class F100005 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String sun = requestParameter.getContent().get("count");
		insert(sun);
		return response;
	}
	private int insert(String sun) throws Exception {
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> para = gson.fromJson(sun, type);

		Integer id = isExist((String) para.get("account"));

		if(id==null){
			Object arg[] = new Object[5];
			arg[0]=para.get("phone");
			arg[1]=para.get("account");
			arg[2]=para.get("pwd");
			arg[3]=para.get("state");
			arg[4]=para.get("dou");
			String sql="INSERT INTO t_count(phone,account,pwd,state,dou,dou_update_time) VALUES (?,?,?,?,?,now())";
			return getNewJdbcTemplate().update(sql,arg);
		}else{
			Object arg[] = new Object[5];
			arg[0]=para.get("phone");
			arg[1]=para.get("pwd");
			arg[2]=para.get("state");
			arg[3]=para.get("dou");
			arg[4]=id;
			String sql="update t_count set phone= ? ,pwd= ? ,state= ? ,dou= ? ,dou_update_time=now() where id=?";
			return getNewJdbcTemplate().update(sql,arg);
		}
	}
	private Integer isExist(String account) throws Exception {

		String sql ="SELECT id FROM t_count where account=? ";
		Integer id = this.getNewJdbcTemplate().queryInt(sql,new String[]{account});
		logger.info("当前ID为："+id);
		return id;
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100005 f = new F100005();
		int i= f.insert("{\"phone\":\"94e433225479\",\"account\":\"18926085629\",\"pwd\":\"123456\",\"state\":\"1\",\"dou\":30}");
		System.out.print(i);
	}
}
