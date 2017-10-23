package com.xxxman.test.select.service;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MyVpnService extends VpnService implements Handler.Callback,Runnable {

    private static final String TAG = MyVpnService.class.getName();
    private Thread mThread;
    private Handler mHandler;
    private ParcelFileDescriptor pf;
    public MyVpnService() {
        Log.d(TAG,"BUILD VPN ");
    }
    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"START VPN  onCreate invoke");
        Builder builder = new Builder();
        builder.setMtu(1500);
        builder.addAddress("192.168.1.2",24);
        builder.addRoute("0.0.0.0",0);
        builder.addDnsServer("114.114.114.114");
        //builder.addSearchDomain(...);
        builder.setSession("MyVpnService");
        //builder.setConfigureIntent(...); //制定一个配置页面
        ParcelFileDescriptor pf = builder.establish();
        protect(8118);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }
        // Start a new session by creating a new thread.
        mThread = new Thread(this, "ToyVpnThread");
        mThread.start();
        return START_STICKY;
    }
    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public synchronized void run() {
        Log.i(TAG,"running vpnService");
        try {
            runVpnConnection();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pf.close();
            } catch (Exception e) {
                // ignore
            }
            pf = null;
            Log.i(TAG, "Exiting");
        }
    }

    private boolean runVpnConnection() throws Exception {
        configure();
        FileInputStream in = new FileInputStream(pf.getFileDescriptor());
        FileOutputStream out = new FileOutputStream(pf.getFileDescriptor());
        ByteBuffer packet = ByteBuffer.allocate(32767);//32KB-1
//        try {
//            int length = in.read(packet.array());
//            Log.d(TAG,"length= "+length);
//            Log.d(TAG,"ByteBuffer= "+packet);
//            out.write(packet.array(), 0, length);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        while (true) {
            boolean idle = true;
            int length = in.read(packet.array());
            if (length > 0) {
                Log.i(TAG,"************new packet");
                System.exit(-1);
                while (packet.hasRemaining()) {
                    Log.i(TAG,""+packet.get());
                    //System.out.print((char) packet.get());
                }
                packet.limit(length);
                //  tunnel.write(packet);
                packet.clear();

                // There might be more outgoing packets.
                idle = false;
            }
            Thread.sleep(50);
        }
    }
    private void configure() throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (pf != null) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        builder.setMtu(1500);
        builder.addAddress("192.168.1.2",24);
        builder.addRoute("0.0.0.0",0);
        builder.addDnsServer("114.114.114.114");
        try {
            pf.close();
        } catch (Exception e) {
            // ignore
        }
        pf = builder.establish();
    }
}
