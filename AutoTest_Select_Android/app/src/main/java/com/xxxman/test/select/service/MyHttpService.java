package com.xxxman.test.select.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xxxman.test.select.util.NanoHttpServer;

import java.io.IOException;

public class MyHttpService extends Service {
    private static final String TAG = MyHttpService.class.getName();
    private NanoHttpServer mHttpServer = null;//这个是HttpServer的句柄。

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        //在这里开启HTTP Server。
        Log.d(TAG,"开始启动httpServer...");
        mHttpServer = new NanoHttpServer(8089);
        try {
            mHttpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        //在这里关闭HTTP Server
        if(mHttpServer != null)
            mHttpServer.stop();
    }
}
