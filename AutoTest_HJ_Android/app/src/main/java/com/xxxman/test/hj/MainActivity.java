package com.xxxman.test.hj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runBtn= (Button) findViewById(R.id.runBtn);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    /**
     * 点击按钮对应的方法
     * @param v
     */
    public void runMyUiautomator(View v){
        Log.i(TAG, "runMyUiautomator: ");
        get_root();
        new UiautomatorThread().start();
    }
    /**
     * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
     */
    class UiautomatorThread extends Thread {
        @Override
        public void run() {
            super.run();
            String command=generateCommand("com.xxxman.test.hj", "HJTest", "test_for");
            command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.test.hj.HJTest1 com.xxxman.test.hj.autotest_hj_android.test/android.support.test.runner.AndroidJUnitRunner";
            CMDUtils.CMD_Result rs= CMDUtils.runCMD(command,true,true);
            Log.e(TAG, "run: " + rs.error + "-------" + rs.success);
        }
        /**
         * 生成命令
         * @param pkgName 包名
         * @param clsName 类名
         * @param mtdName 方法名
         * @return
         */
        public  String generateCommand(String pkgName, String clsName, String mtdName) {
            String command = "am instrument -w -r   -e debug false -e class  "
                   // + pkgName + "." + clsName + "#" + mtdName + " "
                    + pkgName + "." + clsName + " "
                    + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
            Log.e("test1: ", command);
            return command;
        }
    }
    // 获取ROOT权限
    public void get_root(){
        Log.i(TAG, "is_root: "+is_root());
        if (is_root()){
            Toast.makeText(getApplicationContext(), "已经具有ROOT权限!", Toast.LENGTH_LONG).show();
        }
        else{
            try{
                Toast.makeText(getApplicationContext(), "正在获取ROOT权限...", Toast.LENGTH_LONG).show();
                Runtime.getRuntime().exec("su");
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
            }
        }

    }
    // 判断是否具有ROOT权限
    public static boolean is_root() {

        boolean res = false;

        try {
            if ((!new File("/system/bin/su").exists()) &&
                    (!new File("/system/xbin/su").exists())) {
                res = false;
            } else {
                res = true;
            }
            ;
        } catch (Exception e) {

        }
        return res;
        //return false;
    }
}
