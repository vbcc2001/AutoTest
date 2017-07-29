package com.xxxman.autotest.shell;

/**
 * Created by Drizzt on 2017-07-27.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

//继承BroadcastReceiver
public class MyBroadCastReceiver extends BroadcastReceiver{
    //只需重写OnReceiver方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收特定键值的数据，实现广播接收器的基本功能
        String str=intent.getStringExtra("name");
        toast(context,str);
//        Intent tIntent = new Intent(context, MyService.class);
//        //启动指定Service
//        context.startService(tIntent);

    }
    public void toast(Context context,String info){
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
        showMyToast(toast, 9*1000);
    }

    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

}