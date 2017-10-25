package com.xxxman.test.select.util;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class VpnThread extends BaseThread {

    private static final String TAG = VpnThread.class.getName();
    private ParcelFileDescriptor pf;
    private FileInputStream in ;
    private FileOutputStream out;
    private ByteBuffer packet;

    public VpnThread(ParcelFileDescriptor pf) {
        super("VpnThread", false);
        Log.d(TAG,"开始初始化************ df");
        this.pf = pf;
        in = new FileInputStream(pf.getFileDescriptor());
        out = new FileOutputStream(pf.getFileDescriptor());
        packet = ByteBuffer.allocate(32767);//32KB-1
    }
    @Override
    public void process(){
        if(pf==null){
            Log.d(TAG,"pf==null************ ");
        }
        if(in==null){
            Log.d(TAG,"in==null************ ");
        }
        boolean idle = true;
        int length = 0;
        try {
            length = in.read(packet.array());
            if (length > 0) {
                Log.d(TAG,"************ new packet");
                //System.exit(-1);
                while (packet.hasRemaining()) {
                    Log.d(TAG,""+packet.get());
                    System.out.print("vpn--------:"+(char) packet.get());
                }
                packet.limit(length);
                //  tunnel.write(packet);
                packet.clear();
                // There might be more outgoing packets.
                idle = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}