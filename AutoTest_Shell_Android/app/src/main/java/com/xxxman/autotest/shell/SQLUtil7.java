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


public class SQLUtil7 {

    String db_path = Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db";
    SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(db_path,null);
    String TAG = SQLUtil7.class.getName();
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
    public String selectCode(){

        String sql = "select code from code ";
        String code = "";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            code = c.getString(c.getColumnIndex("code"));
        }
        Log.i(TAG, "code为:" + code);
        return code;
    }
    public  void createTableMoney() {
        //创建表SQL语句
        String stu_table = "create table money(id integer primary key autoincrement,number int DEFAULT 0,password string)";
        //执行SQL语句
        db.execSQL(stu_table);
    }
    public int selectMoney(){

        String sql = "select number from money order by id desc ";
        int number = 1;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            number = c.getInt(c.getColumnIndex("number"));
        }
        Log.i(TAG, "number为:" + number);
        return number;
    }
    public String selectPassword(){

        String sql = "select password from money order by id desc ";
        String password = "";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            password = c.getString(c.getColumnIndex("password"));
        }
        Log.i(TAG, "password为:" + password);
        return password;
    }
    public void inserMoney(int number,String password){
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("number",number);
        cv.put("password",password);
        Log.i(TAG, "插入number为:" + number);
        Log.i(TAG, "插入password为:" + password);
        db.insert("money",null,cv);//执行插入操作
    }
}
