package com.xxxman.test.select.service;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.xxxman.test.select.util.BaseThread;
import com.xxxman.test.select.util.VpnThread;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class MyVpnService extends VpnService implements Handler.Callback,Runnable  {

    private static final String TAG = MyVpnService.class.getName();
    private Thread mThread;
    private Handler mHandler;
    private ParcelFileDescriptor pf;
    @Override
    public synchronized void run() {
        try {
            //初始化pf
            if (pf == null) {
                Log.d(TAG,"开始配置vpn...");
                Builder builder = new Builder();
                builder.setMtu(1500);
                builder.addAddress("192.168.10.2",24);
                builder.addRoute("192.168.10.1",32);
                //builder.addRoute("0.0.0.0",0);
                builder.addDnsServer("39.108.120.224");
                //builder.addDnsServer("114.114.114.114");
                builder.setSession("MyVpnService");
                //builder.addSearchDomain(...);
                //builder.setConfigureIntent(...); //制定一个配置页面
                pf = builder.establish();
                //protect(8118);
            }
            Log.d(TAG,"完成配置vpn...");
            FileInputStream in = new FileInputStream(pf.getFileDescriptor());
            FileOutputStream out = new FileOutputStream(pf.getFileDescriptor());

            ByteBuffer packet = ByteBuffer.allocate(Short.MAX_VALUE);//MAX_VALUE = 32767;

            while (true) {
                // Assume that we did not make any progress in this iteration.
                boolean idle = true;
                // Read the outgoing packet from the input stream.
                int length = in.read(packet.array());
                if (length > 0) {
                    Log.i(TAG,"************new packet,length="+length);
                    while (packet.hasRemaining()) {
                        //Log.i(TAG,""+packet.get());
                        System.out.print((char) packet.get());
                    }
                    packet.limit(length);
                    //tunnel.write(packet);
                    packet.clear();
                    // There might be more outgoing packets.
                    idle = false;
                }
                //out.write(packet.array(), 0, length);
                Thread.sleep(50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Log.d(TAG, "关闭vpn描述符...");
                pf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pf = null;
            Log.d(TAG, "退出vpn...");
        }
    }
    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"开始启动vpn...");
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        if (mThread != null) {
            mThread.stop();
        }
        //getLocalIpAddress();
        // Start a new session by creating a new thread.
        mThread = new Thread(this, "VpnThread");
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
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.i(TAG,"****** INET ADDRESS ******");
                    Log.i(TAG,"address: "+inetAddress.getHostAddress());
                    Log.i(TAG,"hostname: "+inetAddress.getHostName());
                    Log.i(TAG,"address.toString(): "+inetAddress.getHostAddress().toString());
                    if (!inetAddress.isLoopbackAddress()) {
                        //IPAddresses.setText(inetAddress.getHostAddress().toString());
                        Log.i(TAG,"IS NOT LOOPBACK ADDRESS: "+inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    } else{
                        Log.i(TAG,"It is a loopback address");
                    }
                }
            }
        } catch (SocketException ex) {
            String LOG_TAG = null;
            Log.e(LOG_TAG, ex.toString());
        }

        return null;
    }
}
