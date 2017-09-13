package com.xxxman.autotest.shell;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SQLUtil9 {

    String db_path = Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db";
    SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(db_path,null);
    String TAG = SQLUtil9.class.getName();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");

    public boolean tabbleIsExist(String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            //这里表名可以是Sqlite_master
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
    //送红包表
    public  void createTableSun() {
        //创建表SQL语句
        String stu_table = "create table sun(id integer primary key autoincrement," +
                "number int DEFAULT 0,phone string,pwd String,day String," +
                "order_id int,huajiao_id String,per_sun int,max_sun int,send_sun int DEFAULT 0," +
                "before_sun int DEFAULT 0,end_sun int DEFAULT 0 )";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    //送红包order表
    public  void createTableOrder() {
        //创建表SQL语句
        String stu_table = "create table order_sun(id integer primary key autoincrement,day string,wait_time int DEFAULT 0,begin_accout int DEFAULT 1,is_talk int DEFAULT 0," +
                "order_id int,huajiao_id int,per_sun int,max_sun int )";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    //初始化order
    public void inserOrder(Order order){
        Log.d(TAG,"插入任务信息为："+order.id+","+order.huajiao_id+","+order.per_dou+","+order.max_dou+","+order.wait_time+","+order.begin_accout+","+order.is_talk);
        String dateString = formatter.format(new Date());
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("day",dateString);
        cv.put("order_id",order.id);
        cv.put("huajiao_id",order.huajiao_id);
        cv.put("per_sun",order.per_dou);
        cv.put("max_sun",order.max_dou);
        cv.put("wait_time",order.wait_time);
        cv.put("begin_accout",order.begin_accout);
        if(order.is_talk){
            cv.put("is_talk",1);
        }
        db.insert("order_sun",null,cv);//执行插入操作
    }
    //查询order
    public Order selectOrder(){
        String sql = "select order_id,huajiao_id,per_sun,max_sun,wait_time,begin_accout,is_talk from order_sun order by id desc LIMIT 1 ";
        Order order = new Order();
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            order.id = c.getInt(c.getColumnIndex("order_id"));
            order.huajiao_id = c.getInt(c.getColumnIndex("huajiao_id"));
            order.per_dou = c.getInt(c.getColumnIndex("per_sun"));
            order.max_dou = c.getInt(c.getColumnIndex("max_sun"));
            order.wait_time = c.getInt(c.getColumnIndex("wait_time"));
            order.begin_accout = c.getInt(c.getColumnIndex("begin_accout"));
            if(c.getInt(c.getColumnIndex("is_talk"))==1){
                order.is_talk = true;
            }
        }
        Log.i(TAG, "order:" + order);
        Log.d(TAG,"任务信息为："+order.id+","+order.huajiao_id+","+order.per_dou+","+order.max_dou+","+order.wait_time+","+order.begin_accout+","+order.is_talk);
        return order;
    }
    public List<User> selectSunUser(Order order) {
        String sql = "select id,number,phone,pwd,send_sun from sun where order_id="+order.id;
        List<User> list = new ArrayList<User>();
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            User user = new User(0, null, null);
            user.id = c.getInt(c.getColumnIndex("id"));
            user.number = c.getInt(c.getColumnIndex("number"));
            user.phone = c.getString(c.getColumnIndex("phone"));
            user.pwd = c.getString(c.getColumnIndex("pwd"));
            user.send_dou = c.getInt(c.getColumnIndex("send_sun"));
            list.add(user);
            Log.i(TAG, "账户为:" + user.id+","+ user.number+","+ user.phone+","+ user.pwd+","+ user.send_dou);
        }
        Log.i(TAG, "任务数为:" + list.size());
        return list;
    }
    //初始化数据
    public void inserSun(List<User> list,Order order){
        String dateString = formatter.format(new Date());
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        for(User user:list){
            cv.put("number",user.number);
            cv.put("phone",user.phone);
            cv.put("pwd",user.pwd);
            cv.put("day",dateString);

            cv.put("order_id",order.id);
            cv.put("huajiao_id",order.huajiao_id);
            cv.put("per_sun",order.per_dou);
            cv.put("max_sun",order.max_dou);

            db.insert("sun",null,cv);//执行插入操作
        }
    }
    public int selectSendSun(int order_id) {
        String sql = "select sum(send_sun) as sum_sun from sun where order_id="+order_id;
        int sum_dou = 0 ;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            sum_dou = c.getInt(c.getColumnIndex("sum_sun"));
        }
        Log.i(TAG, "已送豆数为:" + sum_dou);
        return sum_dou;
    }
    public void updateSun(User user){
        ContentValues cv = new ContentValues();
        cv.put("send_sun", user.send_dou);
        db.update("sun", cv, "id = ?", new String[] { ""+user.id });
    }

}
