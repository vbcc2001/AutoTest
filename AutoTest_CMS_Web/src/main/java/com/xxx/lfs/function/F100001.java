package com.xxx.lfs.function;


import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

/** 添加或修改标签 */
public class F100001 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String phone = requestParameter.getContent().get("phone");
		String tag = requestParameter.getContent().get("tag");
		String register = requestParameter.getContent().get("register");
		insert(register,phone,tag);
		return response;
	}
	private int insert(String register,String phone,String tag) throws Exception {

		Integer id = isExist(phone);

		if(id==null){
			Object arg[] = new Object[3];
			arg[0]=register;
			arg[1]=phone;
			arg[2]=tag;
			String sql="INSERT INTO t_register(register,phone,tag,state,update_time) VALUES (?,?,?,'1',now())";
			return getNewJdbcTemplate().update(sql,arg);
		}else{
			Object arg[] = new Object[4];
			arg[0]=register;
			arg[1]=phone;
			arg[2]=tag;
			arg[3]=id;
			String sql="update t_register set register=? ,phone=? ,tag=? ,update_time=now() where id=?";
			return getNewJdbcTemplate().update(sql,arg);
		}
	}
	private Integer isExist(String phone) throws Exception {

		String sql ="SELECT id FROM t_register where phone=? ";
		Integer id = this.getNewJdbcTemplate().queryInt(sql,new String[]{phone});
		logger.info("当前ID为："+id);
		return id;
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100001 f = new F100001();
		f.insert("544e5f7be0f3761502f51b6486ba776","c4c8ba9f4fd2","Test-4X");
	}
}
