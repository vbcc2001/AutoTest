package com.xxxman.autotest.shell;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class MoneyActivity extends AppCompatActivity {


    private static final String TAG = "MoneyActivity";
    private EditText mEmailView;
    private EditText mPwdView;
    boolean is_code = false;
    String code;
    SQLUtil7 sqlUtil = new SQLUtil7();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        mEmailView = (EditText) findViewById(R.id.email);
        mPwdView = (EditText) findViewById(R.id.pwd);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goMoney(view,false);
            }
        });
        Button closeInButton = (Button) findViewById(R.id.close_in_button);
        closeInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goMoney(view,true);
            }
        });


        //判断是否注册
        String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                "ioZotQIDAQAB";
        RSAUtils.loadPublicKey(pubkey);
        String value = sqlUtil.selectCode();
        String sn = SNUtil.getuniqueId(this);
        String enctytCode = null;
        try {
            enctytCode = RSAUtils.encryptWithRSA(sn);
            code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            code= code.substring(0,12);
            Log.d(TAG,code);
            Log.d(TAG,value);
            is_code = ("@"+code).equals(value);
            if(value.length()==12){
                is_code = code.equals(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goMoney(View v,boolean is_close) {
        //创建表
        if (!sqlUtil.tabbleIsExist("money")){
            sqlUtil.createTableMoney();
        }

        if(is_code){
            String number_string = mEmailView.getText().toString().trim();
            int number = 1;
            if(!"".equals(number_string)){
                number = Integer.parseInt(number_string);
            }
            String pwd = mPwdView.getText().toString().trim();
            if(!"".equals(pwd)){
                pwd = "@"+pwd;
            }
            sqlUtil.inserMoney(number,pwd);
            if(is_close){
                UiautomatorThread4 thread4 = new UiautomatorThread4();
            }else{
                UiautomatorThread3 thread3 = new UiautomatorThread3();
            }
            Log.i(TAG, "runMyUiautomator: ");

        }else {
            Toast.makeText(this, "请先注册！", Toast.LENGTH_LONG).show();
        }
    }

    static class UiautomatorThread3 extends BaseThread {

        public UiautomatorThread3() {
            super("HJTest7", false);
        }
        @Override
        public void process() {
            //super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest7 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }

    }
    static class UiautomatorThread4 extends BaseThread {

        public UiautomatorThread4() {
            super("HJTest8", false);
        }
        @Override
        public void process() {
            //super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest8 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }

    }
}

