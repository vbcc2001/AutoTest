package com.xxxman.autotest.shell;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "MainActivity_HJ";
    Button runBtn;
    Button getRoot;
    TextView rootTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runBtn = (Button) findViewById(R.id.runBtn);
        getRoot = (Button) findViewById(R.id.getRoot);
        rootTextView = (TextView) findViewById(R.id.root_view);

        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            rootTextView.setText("已经获取Root权限！" );
            rootTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    public void runMyUiautomator(View v) {
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
