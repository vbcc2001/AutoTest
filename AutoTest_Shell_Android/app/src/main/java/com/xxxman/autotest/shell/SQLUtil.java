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



public class SQLUtil {

    String db_path = Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db";
    SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(db_path,null);
    String TAG = SQLUtil.class.getName();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    String dateString = formatter.format(new Date());

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
    public  void createTableCount() {
        //创建表SQL语句
        String stu_table = "create table count(id integer primary key autoincrement," +
                "phone string,pwd String,day String, task_count int,end_count int,success_count int)";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    public List<User> selectUser() {
        String sql = "select id,phone,pwd from count where task_count=0 and day = '"+dateString+"'";
        List<User> list = new ArrayList<User>();
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            User user = new User(0, null, null);
            user.id = c.getInt(c.getColumnIndex("id"));
            user.phone = c.getString(c.getColumnIndex("phone"));
            user.pwd = c.getString(c.getColumnIndex("pwd"));
            list.add(user);
        }
        Log.i(TAG, "任务数为:" + list.size());
        return list;
    }
    public void inserCount(List<User> list){
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        for(User user:list){
            cv.put("phone",user.phone);
            cv.put("pwd",user.pwd);
            cv.put("day",dateString);
            cv.put("task_count",0);
            cv.put("end_count",0);
            cv.put("success_count",0);
            db.insert("count",null,cv);//执行插入操作
        }
    }
    public void updateTaskCount(User user){
        ContentValues cv = new ContentValues();
        cv.put("task_count", 1);
        db.update("count", cv, "id = ?", new String[] { ""+user.id });
    }
    public void updateSuccessCount(User user){
        ContentValues cv = new ContentValues();
        cv.put("success_count", 1);
        db.update("count", cv, "id = ?", new String[] { ""+user.id });
    }
    public void updateEndCount(User user){
        ContentValues cv = new ContentValues();
        cv.put("end_count", 1);
        db.update("count", cv, "id = ?", new String[] { ""+user.id });
    }
    public int selectUserCount(){

        String sql = "select count(1) as sum from count where day = '"+dateString+"'";
        int sum = 0;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            sum = c.getInt(c.getColumnIndex("sum"));
        }
        Log.i(TAG, "任务数为:" + sum);
        return sum;
    }
    public int selectTaskCount(){
        String sql = "select count(1) as sum from count where task_count=0 and day = '"+dateString+"'";
        int sum = 0;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            sum = c.getInt(c.getColumnIndex("sum"));
        }
        Log.i(TAG, "执行任务数为:" + sum);
        return sum;
    }
    public int selectEndTaskCount(){
        String sql = "select count(1) as sum from count where task_count=1 and day = '"+dateString+"'";
        int sum = 0;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            sum = c.getInt(c.getColumnIndex("sum"));
        }
        Log.i(TAG, "执行任务数为:" + sum);
        return sum;
    }
    public int selectSuccessCount(){
        String sql = "select count(1) as sum from count where success_count=1 and day = '"+dateString+"'";
        int sum = 0;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            sum = c.getInt(c.getColumnIndex("sum"));
        }
        Log.i(TAG, "成功任务数为:" + sum);
        return sum;
    }
    public List<User> selectFailCount(){
        String sql = "select id,phone,pwd  from count where task_count=1 and  success_count=0 and day = '"+dateString+"'";
        List<User> list = new ArrayList<User>();
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            User user = new User(0, null, null);
            user.id = c.getInt(c.getColumnIndex("id"));
            user.phone = c.getString(c.getColumnIndex("phone"));
            user.pwd = c.getString(c.getColumnIndex("pwd"));
            list.add(user);
        }
        Log.i(TAG, "失败任务数为:" + list.size());
        return list;
    }
}
