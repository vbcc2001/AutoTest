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


public class SQLUtil2    {

    String db_path = Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db";
    SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(db_path,null);
    String TAG = SQLUtil2.class.getName();
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
    public  void createTableHongbao() {
        //创建表SQL语句
        String stu_table = "create table hongbao(id integer primary key autoincrement," +
                "number int DEFAULT 0,phone string,pwd String,day String," +
                "order_id int,huajiao_id String,per_dou int,max_dou int,before_dou int DEFAULT 0,end_dou int DEFAULT 0 )";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    //送红包order表
    public  void createTableOrder() {
        //创建表SQL语句
        String stu_table = "create table order(id integer primary key autoincrement,day String," +
                "order_id int,huajiao_id String,per_dou int,max_dou int )";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    //初始化order
    public void inserOrder(Order order){
        String dateString = formatter.format(new Date());
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("day",dateString);
        cv.put("order_id",order.id);
        cv.put("huajiao_id",order.huajiao_id);
        cv.put("per_dou",order.per_dou);
        cv.put("max_dou",order.max_dou);
        db.insert("order",null,cv);//执行插入操作
    }
    //查询order
    public Order selectOrder(){
        String sql = "select order_id,huajiao_id,per_dou,max_dou from order order by id desc LIMIT 1 ";
        Order order = new Order();
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            order.id = c.getInt(c.getColumnIndex("order_id"));
            order.huajiao_id = c.getInt(c.getColumnIndex("huajiao_id"));
            order.per_dou = c.getInt(c.getColumnIndex("per_dou"));
            order.max_dou = c.getInt(c.getColumnIndex("max_dou"));
        }
        Log.i(TAG, "order:" + order);
        return order;
    }
    //初始化数据
    public void inserHongbao(List<User> list,Order order){
        String dateString = formatter.format(new Date());
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        for(User user:list){
            cv.put("number",user.number);
            cv.put("phone",user.phone);
            cv.put("pwd",user.pwd);
            cv.put("day",dateString);

            cv.put("order_id",order.id);
            cv.put("huajiao_id",order.huajiao_id);
            cv.put("per_dou",order.per_dou);
            cv.put("max_dou",order.max_dou);

            db.insert("hongbao",null,cv);//执行插入操作
        }
    }
    public List<User> selectHongbaopUser(int order_id) {
        String sql = "select id,number,phone,pwd from hongbao where order_id="+order_id;
        List<User> list = new ArrayList<User>();
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            User user = new User(0, null, null);
            user.id = c.getInt(c.getColumnIndex("id"));
            user.number = c.getInt(c.getColumnIndex("number"));
            user.phone = c.getString(c.getColumnIndex("phone"));
            user.pwd = c.getString(c.getColumnIndex("pwd"));
            list.add(user);
        }
        Log.i(TAG, "任务数为:" + list.size());
        return list;
    }

}
