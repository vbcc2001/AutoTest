package com.xxxman.autotest.shell;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

public class MyService extends Service {
    public static final String TAG = "MyService";
    public BaseThread thread;
    private MyBinder binder=new MyBinder();
    private final Random random=new Random();
    public void setThread(BaseThread thread){
        this.thread= thread;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "in onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "in onStartCommand");
        String name = intent.getStringExtra("name");
        Log.d(TAG, "name:" + name);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "in onDestroy");
    }
    //  service服务中的方法
    public void toast(String info){
        Toast.makeText(getApplicationContext(), "当前任务为"+info, Toast.LENGTH_LONG).show();
    }

    public class MyBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "-MyService-readInt->"+data.readInt(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "-MyService-readString->"+data.readString(), Toast.LENGTH_LONG).show();
            reply.writeString("rose");
            reply.writeInt(123);
            return super.onTransact(code, data, reply, flags);
        }
    }

//    try {
//        Thread.sleep(10000);
//        Log.i(TAG, "runMyUiautomator: 10s----");
//        thread3.suspend();
//        Thread.sleep(10000);
//        Log.i(TAG, "runMyUiautomator: 20s----");
//        thread3.resume();
//    } catch (InterruptedException e) {
//        e.printStackTrace();
//    }
}