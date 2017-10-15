package com.xxxman.test.select.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tuzi on 2017/10/16.
 */

public class ToastUitl {

    public static void toast(Context context, String info){
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
        LinearLayout layout = (LinearLayout) toast.getView();
        layout.setBackgroundColor(Color.parseColor("#000000"));
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.RED);
        v.setTextSize(16);
        showMyToast(toast, 9000);
    }

    private static void showMyToast(final Toast toast, final int cnt) {
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

    public static void  sendBroadcast(Context context, String info) {
        Intent intent = new Intent();
        intent.setAction("com.xxxman.test.select.MyBroadCastReceiver");
        intent.putExtra("name", info);
        context.sendBroadcast(intent);
    }
}
