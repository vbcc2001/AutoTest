package com.xxx.lfs.function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.jdbc.DBConfigure;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 添加阳光账号信息
 */
public class F100008 extends BaseFunction  {
    @Override
    public ResponseParameter execute(RequestParameter request) throws Exception {
        String phone = request.getContent().get("phone");
        String list = request.getContent().get("list");
        insert(phone,list);
        return response;
    }
    private void insert(String phone,String list) throws Exception {

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Type type = new TypeToken<List<Map<String,String>>>() {}.getType();
        List<Map<String,String>> accoutList= gson.fromJson(list, type);

        //删除旧的信息
        Object arg[] = new Object[1];
        arg[0]=phone;
        String sql="delete from t_accout where type='sun' and phone=? ";
        this.getNewJdbcTemplate().update(sql,arg);

        //插入
        for(Map<String,String> dataRow : accoutList){
            if(dataRow!=null){
                Object arg1[] = new Object[3];
                arg1[0]=phone;
                arg1[1]=dataRow.get("n");
                arg1[2]=dataRow.get("a");
                sql="INSERT INTO t_accout(phone,number,accout,type,state,update_time) VALUES (?,?,?,'sun','1',now())";
                this.getNewJdbcTemplate().update(sql,arg1);
            }
        }
    }
    public static void main(String arg[] ) throws Exception{
        new DBConfigure().loadConfig();
        F100008 f = new F100008();
        f.insert("c4c8ba9f4fd2","[{n:1,a:15889649769},{n:2,a:15889649769},]");
    }
}
