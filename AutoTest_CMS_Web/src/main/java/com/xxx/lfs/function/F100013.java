package com.xxx.lfs.function;

import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

/** 添加或修改更新 */
public class F100013 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String version = requestParameter.getContent().get("version");
		String remark = requestParameter.getContent().get("remark");
		String url = requestParameter.getContent().get("url");
		String type = requestParameter.getContent().get("type");
		insert(version,remark,url,type);
		return response;
	}
	private int insert(String version,String remark,String url,String type) throws Exception {

		Integer id = isExist(version);

		if(id==null){
			Object arg[] = new Object[4];
			arg[0]=version;
			arg[1]=remark;
			arg[2]=url;
			arg[3]=type;
			String sql="INSERT INTO t_update(version,remark,url,type,state,update_time) VALUES (?,?,?,?,'1',now())";
			return getNewJdbcTemplate().update(sql,arg);
		}else{
			Object arg[] = new Object[5];
			arg[0]=version;
			arg[1]=remark;
			arg[2]=url;
			arg[3]=type;
			arg[4]=id;
			String sql="update t_update set version=? ,remark=? ,url=? ,type=? ,update_time=now() where id=?";
			return getNewJdbcTemplate().update(sql,arg);
		}
	}
	private Integer isExist(String version) throws Exception {

		String sql ="SELECT id FROM t_update where version=? ";
		Integer id = this.getNewJdbcTemplate().queryInt(sql,new String[]{version});
		logger.info("当前ID为："+id);
		return id;
	}
	public static void main(String arg[] ) throws Exception{
		new DBConfigure().loadConfig();
		F100013 f = new F100013();
		f.insert("花椒助手_2.0_","测试12","http://www.163.com","助手");
	}
}
