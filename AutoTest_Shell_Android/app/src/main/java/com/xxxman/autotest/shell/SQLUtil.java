package com.xxxman.autotest.shell;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Drizzt on 2017-07-12.
 */

public class SQLUtil {

    public static boolean tabbleIsExist(SQLiteDatabase db, String tableName){
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
    public static  void createTable(SQLiteDatabase db) {
        //创建表SQL语句
        String stu_table = "create table count(_id integer primary key autoincrement,task_count integer,suess_count integer,fail_count integer)";
        //执行SQL语句
        db.execSQL(stu_table);
    }
}
