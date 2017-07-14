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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "MainActivity_HJ";
    Button runBtn;
    Button getRoot;
    TextView rootTextView;
    TextView taskView;
    SQLUtil sqlUtil = new SQLUtil();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runBtn = (Button) findViewById(R.id.runBtn);
        getRoot = (Button) findViewById(R.id.getRoot);

        rootTextView = (TextView) findViewById(R.id.root_view);
        taskView =  (TextView) findViewById(R.id.task_view);

        if (!sqlUtil.tabbleIsExist("count")){
            sqlUtil.createTableCount();
        }
        //读取txt文档
        if(sqlUtil.selectUserCount()==0){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt");
                Log.d(TAG,"user_list.txt中用户数量："+list.size());
                sqlUtil.inserCount(list);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
            }
        }

        int user_count = 0;
        int task_count = 0;
        int end_task_count = 0;
        //int end_count = 0;
        int success_count = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        user_count =sqlUtil.selectUserCount();
        task_count =sqlUtil.selectTaskCount();
        end_task_count =sqlUtil.selectEndTaskCount();
        success_count =sqlUtil.selectSuccessCount();
        taskView.setText(dateString+":总任务数"+user_count+"\n待处理数"+task_count+"，已处理"+end_task_count+"个，成功"+success_count+"个!" );

        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            rootTextView.setText("已经获取Root权限！" );
            rootTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    public void runMyUiautomator(View v) {
        if (sqlUtil.selectFailCount().size()>0) {
            new UiautomatorThread().start();
            Log.i(TAG, "runMyUiautomator: ");
        }else{
            Toast.makeText(getApplicationContext(), "无未完成任务可运行！", Toast.LENGTH_LONG).show();
        }
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
