package com.xxxman.autotest.shell;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "MainActivity_HJ";
    Button runBtn;
    Button getRoot;
    TextView rootTextView;
    TextView taskView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runBtn = (Button) findViewById(R.id.runBtn);
        getRoot = (Button) findViewById(R.id.getRoot);

        rootTextView = (TextView) findViewById(R.id.root_view);
        taskView =  (TextView) findViewById(R.id.task_view);

        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,path);
        List<FileUtil.UserRecord> list = FileUtil.ReadTxtFile(path+"/user_list.txt");
        Log.d(TAG,"user_list.txt中用户数量："+list.size());

        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db",null);
        if (!SQLUtil.tabbleIsExist(db,"count")){
            SQLUtil.createTable(db);
        }
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("task_count",list.size());
        cv.put("suess_count",0);
        cv.put("fail_count",0);
        db.insert("count",null,cv);//执行插入操作
        taskView.setText("已完成0个，待处理"+list.size()+"个!" );


        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            rootTextView.setText("已经获取Root权限！" );
            rootTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    public void runMyUiautomator(View v) {
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("task_count",0);
        cv.put("suess_count",0);
        cv.put("fail_count",0);
        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(Environment.getDataDirectory()+"/data/com.xxxman.autotest.shell/hj.db",null);
        if (SQLUtil.tabbleIsExist(db,"count")){
            String sql =  "select task_count,suess_count,fail_count  from count order by _id desc limit 0,1";
            Cursor c  = db.rawQuery(sql,null);
            while (c.moveToNext())
            {
                cv.put("task_count",c.getInt(c.getColumnIndex("task_count")));
                cv.put("suess_count",c.getInt(c.getColumnIndex("suess_count")));
                cv.put("fail_count",c.getInt(c.getColumnIndex("fail_count")));
            }
            Log.i(TAG, "任务数为:"+cv.get("task_count"));
        }
        Log.i(TAG, "runMyUiautomator: ");
        //get_root();
        new UiautomatorThread().start();
    }

    /**
     * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
     */
    class UiautomatorThread extends Thread {
        @Override
        public void run() {
            super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest1 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.e(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
    // 获取ROOT权限
    public void get_root(View v) {
            try {
                Toast.makeText(getApplicationContext(), "正在获取ROOT权限...", Toast.LENGTH_LONG).show();
                Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
            }
    }
}
