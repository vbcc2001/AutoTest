package com.xxxman.test.select.service;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.xxxman.test.select.util.BaseThread;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MyVpnService extends VpnService implements Handler.Callback {

    private static final String TAG = MyVpnService.class.getName();
    private BaseThread thread;
    private Handler mHandler;
    private ParcelFileDescriptor pf;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        if (thread != null) {
            thread.stop();
        }
        thread = new BaseThread("VpnThread", false) {
            @Override
            public void process() {
                Log.d(TAG,"running vpnService");
                try {
                    runVpnConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        pf.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pf = null;
                    Log.d(TAG, "Exiting");
                }
            }
        };
        return START_STICKY;
    }
    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean runVpnConnection() throws Exception {
        //初始化pf
        if (pf == null) {
            Log.d(TAG,"Start VPN Configure");
            Builder builder = new Builder();
            builder.setMtu(1500);
            builder.addAddress("192.168.1.2",24);
            builder.addRoute("0.0.0.0",0);
            builder.addDnsServer("114.114.114.114");
            builder.setSession("MyVpnService");
            //builder.addSearchDomain(...);
            //builder.setConfigureIntent(...); //制定一个配置页面
            pf = builder.establish();
            //protect(8118);
        }
        FileInputStream in = new FileInputStream(pf.getFileDescriptor());
        FileOutputStream out = new FileOutputStream(pf.getFileDescriptor());
        ByteBuffer packet = ByteBuffer.allocate(32767);//32KB-1
        while (true) {
            boolean idle = true;
            int length = in.read(packet.array());
            if (length > 0) {
                Log.d(TAG,"************ new packet");
                System.exit(-1);
                while (packet.hasRemaining()) {
                    Log.d(TAG,""+packet.get());
                    System.out.print((char) packet.get());
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
}
