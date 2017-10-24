package com.xxxman.test.select.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.SQLUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskSQL  {

    private static String TAG = TaskSQL.class.getName();
    public static String table = "t_task";

    /** 创建任务表 **/
    public static void createTableTask() {
        //创建表SQL语句
        String stu_table = "create table "+table+"(id integer primary key autoincrement,"
                + "number int,"
                + "uid int,"
                + "phone string,"
                + "pwd string,"
                +"day string, "
                +"task_count int DEFAULT 0,"
                +"success_count int DEFAULT 0, "
                +"fail_count int DEFAULT 0, "
                +"type string)";
        //执行SQL语句
        SQLUtil.getDB().execSQL(stu_table);
    }
    /** 插入任务 **/
    public static void inserTask(List<Task> list, String type){
        ContentValues cv = new ContentValues();
        for(Task task:list){
            cv.put("number",task.getNumber());
            cv.put("uid",task.getUid());
            cv.put("phone",task.getPhone());
            cv.put("pwd",task.getPwd());
            cv.put("day",SQLUtil.getDayString());
            cv.put("task_count", 0);
            cv.put("success_count",0);
            cv.put("fail_count",0);
            cv.put("type",type);
            SQLUtil.getDB().insert(table,null,cv);//执行插入操作
        }
        Log.i(TAG, "插入任务数为:" + list.size());
    }
    public static int selectTaskCount(String type){

        String sql = "select count(1) as sum from "+table+" where success_count=0 and type='"+type+"' and day = '"+SQLUtil.getDayString()+"'";
        int sum = 0;
        Cursor c = SQLUtil.getDB().rawQuery(sql, null);
        while (c.moveToNext()) {
            sum = c.getInt(c.getColumnIndex("sum"));
        }
        Log.i(TAG, "任务总数为:" + sum);
        return sum;
    }
    public static List<Task>  selectTask(String type){

        String sql = "select * from "+table+" where  type='"+type+"'and day = '"+SQLUtil.getDayString()+"'";
        List<Task> list = new ArrayList<Task>();
        Cursor c = SQLUtil.getDB().rawQuery(sql, null);
        while (c.moveToNext()) {
            Task task = new Task();
            task.setId(c.getInt(c.getColumnIndex("id")));
            task.setNumber(c.getInt(c.getColumnIndex("number")));
            task.setUid(c.getInt(c.getColumnIndex("uid")));
            task.setPhone( c.getString(c.getColumnIndex("phone")));
            task.setPwd( c.getString(c.getColumnIndex("pwd")));
            task.setDay( c.getString(c.getColumnIndex("day")));
            task.setTask_count(c.getInt(c.getColumnIndex("task_count")));
            task.setSuccess_count(c.getInt(c.getColumnIndex("success_count")));
            task.setFail_count(c.getInt(c.getColumnIndex("fail_count")));
            task.setType( c.getString(c.getColumnIndex("type")));
            list.add(task);
        }
        Log.i(TAG, "待任务数为:" + list.size());
        return list;
    }
    /**更新任务数**/
    public static void updateTaskCount(int id ,String name,int value ){
        ContentValues cv = new ContentValues();
        cv.put(name, value);
        SQLUtil.getDB().update(table, cv, "id = ?", new String[] { ""+id });
        Log.i(TAG, "更新任务:" +id+"--"+name+"--"+value);
    }
}
