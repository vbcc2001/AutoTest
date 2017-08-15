package com.xxxman.autotest.shell;

/**
 * Created by Drizzt on 2017-07-27.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//继承BroadcastReceiver
public class BootBroadCastReceiver extends BroadcastReceiver{

    SQLUtil1 sqlUtil = new SQLUtil1();
    private static final String TAG = BootBroadCastReceiver.class.getName();
    boolean is_code = false;

    //只需重写OnReceiver方法
    @Override
    public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "进入开机启动: 10s----"+intent.getAction());
            if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
                Log.i(TAG, "进入开机启动: 10s----");
                try {
                    Thread.sleep(10000);
                    //Intent intent2 = new Intent(context, MainActivity.class);
                    //context.startActivity(intent2);
                    //Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.xxxman.autotest.shell");
                    //context.startActivity(intent1);
                    //Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //创建表
                if (!sqlUtil.tabbleIsExist("hongbao")){
                    sqlUtil.createTableCount();
                }
                //读取txt文档，插入到表
                if(sqlUtil.selectUserCount()==0){
                    try {
                        String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                        List<User> list = FileUtil.ReadTxtFile(path+"/bh_NumberList.txt");
                        Log.d(TAG,"user_list.txt中用户数量："+list.size());
                        sqlUtil.inserCount(list);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "读取用户文件出错！", Toast.LENGTH_LONG).show();
                    }
                }

                //判断是否注册
                String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                        "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                        "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                        "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                        "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                        "ioZotQIDAQAB";
                RSAUtils.loadPublicKey(pubkey);
                String value = sqlUtil.selectCode();
                String sn = SNUtil.getuniqueId(context);
                String enctytCode = null;
                try {
                    enctytCode = RSAUtils.encryptWithRSA(sn);
                    String code = SNUtil.getMD5(enctytCode);
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

                if(is_code){
                    if (sqlUtil.selectHongbaoUser().size() >0 || sqlUtil.selectHongbaoFailUser().size()>0) {
                        HongbaoFragment.UiautomatorThread3 thread3 = new HongbaoFragment.UiautomatorThread3();
                        Log.i(TAG, "runMyUiautomator: ");
                        try {
                            Thread.sleep(10000);
                            Log.i(TAG, "runMyUiautomator: 10s----");
                            thread3.suspend();
                            Thread.sleep(10000);
                            Log.i(TAG, "runMyUiautomator: 20s----");
                            thread3.resume();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(context, "无未完成任务可运行！", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "请先注册！", Toast.LENGTH_LONG).show();
                }
            }
    }
}