package com.xxxman.autotest.shell;


import android.content.ContentValues;
import android.content.SharedPreferences;
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
        if (sqlUtil.selectTaskCount()>0 || sqlUtil.selectFailCount().size()>0) {
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
    public void register(View v){
            //Toast.makeText(getApplicationContext(), "口令为："+SNUtil.getuniqueId(v.getContext()), Toast.LENGTH_LONG).show();
            //RSAUtils.generateRSAKeyPair();

            String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                    "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                    "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                    "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                    "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                    "ioZotQIDAQAB";
            String priKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDbOawumY+ZqZZyPOe+KKGF5e15\n" +
                    "xEkJBeOfuuOrk5Cxwm8r6crTZICOlLLUYFfgk1UkosgL1cS0oewHqkYxcr4L2YdJZiGAXYnBerKU" +
                    "Nc/EFlHf8kusk71rA3wmf0EswB5cKekZ+4afMf1FKKb365elEHCNuXLtgJtZkRj7mfXx0NCVc/hA" +
                    "E7KHuMTSkph5eguRJ2BY/E4vm1+6JKN7AbTrExb57Y5AO9EVSE6BAzjV4/yoX0oVoV25TMSKJDnN" +
                    "gdQcfELiyqsgezUfNklPrvTVuHgLclg4A+j+PqonDecS2Nv46jYBWoZxCvk+wZwf1sagdJdi6WuH" +
                    "JkJZAKqKhmi1AgMBAAECggEBAJurS1nXz0GVS/CY0RKV9YSILeZefGI83VLKOerXIVMotxqerFkJ" +
                    "r8QPUSE/vIcK99XJBXZp+IEvzdPvlGJ+kPcHI2r6a+WkBjLudqqJv5wFIWR9wECutD2uPtVzXYty" +
                    "bNyTIiRCGGko7SjT4iSAFbGvh80Ll9GQlj+2qd/Xhu6LQWKj573rV5VhcEwDq6PAxWE+pGoYLn1b" +
                    "ztHRFMeneR71maF1dl71f7OHwff/zIfk5Mu7n+dBxUwedOGUs3eAQNS36w+7/NtrSqHAmpiYj9Ck" +
                    "IbguDq7ivQTM4Ar/SNwkz0q2RKwJ6hw4sxEcvLiEMbx4yg1vM6kJqh4KM7ICi8ECgYEA8SlWUKR8" +
                    "rW4Hwx3i0a6QX1mpqTPYrbnKmTwCI967NxWVfVk7U/wzR00pr7zGz64hUnfvO+P4DdZnmve3yMok" +
                    "iRnTeVEs2mvlcEyUe1WDOxudAIIlVHxQNCOw6Uac5qP0MHpWRHlSTjNEp/IOa5kPLvvhUYZ89dcQ" +
                    "oYVhylUX+XECgYEA6LbOivFqgjHRldnJGXs3sVJUkHanuuaKVYa47O7qXK67bC8DFQyW6VQS0Vrm" +
                    "DDN8d9+l0ZcS93mQOhot92KLVDxYHGY2m5Xk5oK6jqM7bbfzgXVa/k3VbZioaBjH5XFjEzDfiqZm" +
                    "X7zcHGctZpKJJ5V3dmO9yTVLfj+9saFuYYUCgYEAywRUoJDIUKvXJv/KyWAeM9bkiAeYei91CejF" +
                    "mHLRwj6OWTa8RiiC9pxT4piV+ZGKhcVnhVCVqvh6wa+WbRcXCL/QEkou6zV3skEVonpLfn/xfNMT" +
                    "H/uC/VGqhccnINaXJBRo+T309tYcDxIr55KzgIcUmLASFFdXrdH+j/lwtFECgYAnr0T5nMG1Ahnj" +
                    "nAgXOFP/ATM6j4F69eWRQDA492Uv+PwtLrcv173EfHnZCc9BNWZ8ar80RrcNTMWzotND5KIt8zxz" +
                    "W1rknWMzjAeUW3G+/CeiZAjoZQ2IawgM+GzeS7/Bfgwg8M90dBh1H4M2graw8WQ15DxxG42MMgJ/" +
                    "UDAqoQKBgQCOb2GyBjpLylRl+o8TEbIXzCE35n7QtyOOhl7XbvGPjpi8BLagvBh9f80m7ZN0bfum" +
                    "1VBH6HjxCYS0zXnUnrqNHaGR4I2GZHafMkZ4YZPMxmz7K1YWBE0+Vyr0wIOkzdnzXzjmDlWjRdQU" +
                    "fDLnED0zTJlv0Y8mHNs76XcWdqi1Rw==";
            RSAUtils.loadPublicKey(pubkey);
            RSAUtils.loadPrivateKey(priKey);
            String enctytCode = null;
            try {
                String sn_code = SNUtil.getuniqueId(v.getContext());
                enctytCode = RSAUtils.encryptWithRSA(sn_code);
                Log.d(TAG,sn_code);
                Log.d(TAG,enctytCode);
                Log.d(TAG,RSAUtils.decryptWithRSA(enctytCode));
                Toast.makeText(getApplicationContext(), "RSA:"+RSAUtils.decryptWithRSA(enctytCode), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


//            //步骤1：获取输入值
//            String code = "123";
//            //步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
//            SharedPreferences.Editor editor = getSharedPreferences("lock", MODE_WORLD_WRITEABLE).edit();
//            //步骤2-2：将获取过来的值放入文件
//            editor.putString("code", code);
//            //步骤3：提交
//            editor.commit();
//            Toast.makeText(getApplicationContext(), "口令设置成功", Toast.LENGTH_LONG).show();
//            //步骤1：创建一个SharedPreferences接口对象
//            SharedPreferences read = getSharedPreferences("lock", MODE_WORLD_READABLE);
//            //步骤2：获取文件中的值
//            String value = read.getString("code", "");
//            //Toast.makeText(getApplicationContext(), "口令为："+value, Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "口令为："+SNUtil.getuniqueId(v.getContext()), Toast.LENGTH_LONG).show();
    }

}
