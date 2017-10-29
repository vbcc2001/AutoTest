package com.xxxman.test.select.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.object.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLUtil {

    private static String  db_path = Environment.getDataDirectory()+"/data/com.xxxman.test.select/hj.db";
    private static String TAG = SQLUtil.class.getName();
    private static SQLiteDatabase db = null;
    public static boolean tabbleIsExist(String tableName){
        db= SQLUtil.getDB();
        boolean result = false;
        Cursor cursor = null;
        if(tableName == null){
            return false;
        }else{
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
                e.printStackTrace();
            }
            return result;
        }
    }
    public static SQLiteDatabase getDB(){
        if(db==null) {
           db = SQLiteDatabase.openOrCreateDatabase(db_path,null);
        }
        return db ;
    }
    public static String getDayString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        return dateString;
    }
    public static String getDayString2(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String dateString = formatter.format(new Date());
        return dateString;
    }
}
