package com.xxxman.test.select.service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.xxxman.test.select.R;
import com.xxxman.test.select.menu.VpnActivity;
import com.xxxman.test.select.util.ToyVpnConnection;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
public class ToyVpnService extends VpnService implements Handler.Callback {
    private static final String TAG = ToyVpnService.class.getSimpleName();
    public static final String ACTION_CONNECT = "com.example.android.toyvpn.START";
    public static final String ACTION_DISCONNECT = "com.example.android.toyvpn.STOP";
    private Handler mHandler;
    private static class Connection extends Pair<Thread, ParcelFileDescriptor> {
        public Connection(Thread thread, ParcelFileDescriptor pfd) {
            super(thread, pfd);
        }
    }
    private final AtomicReference<Thread> mConnectingThread = new AtomicReference<>();
    private final AtomicReference<Connection> mConnection = new AtomicReference<>();
    private AtomicInteger mNextConnectionId = new AtomicInteger(1);
    private PendingIntent mConfigureIntent;
    @Override
    public void onCreate() {
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        mConfigureIntent = PendingIntent.getActivity(this, 0, new Intent(this, VpnActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_DISCONNECT.equals(intent.getAction())) {
            disconnect();
            return START_NOT_STICKY;
        } else {
            connect();
            return START_STICKY;
        }
    }
    @Override
    public void onDestroy() {
        disconnect();
    }
    @Override
    public boolean handleMessage(Message message) {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        if (message.what != R.string.disconnected) {
            updateForegroundNotification(message.what);
        }
        return true;
    }
    private void connect() {
        // Become a foreground service. Background services can be VPN services too, but they can
        // be killed by background check before getting a chance to receive onRevoke().
        updateForegroundNotification(R.string.connecting);
        mHandler.sendEmptyMessage(R.string.connecting);
        // Extract information from the shared preferences.
        final SharedPreferences prefs = getSharedPreferences(VpnActivity.Prefs.NAME, MODE_PRIVATE);
        final String server = prefs.getString(VpnActivity.Prefs.SERVER_ADDRESS, "");
        final byte[] secret = prefs.getString(VpnActivity.Prefs.SHARED_SECRET, "").getBytes();
        final int port;
        try {
            port = Integer.parseInt(prefs.getString(VpnActivity.Prefs.SERVER_PORT, ""));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Bad port: " + prefs.getString(VpnActivity.Prefs.SERVER_PORT, null), e);
            return;
        }
        // Kick off a connection.
        startConnection(new ToyVpnConnection(this, mNextConnectionId.getAndIncrement(), server, port, secret));
    }
    private void startConnection(final ToyVpnConnection connection) {
        // Replace any existing connecting thread with the  new one.
        final Thread thread = new Thread(connection, "ToyVpnThread");
        setConnectingThread(thread);
        // Handler to mark as connected once onEstablish is called.
        connection.setConfigureIntent(mConfigureIntent);
        connection.setOnEstablishListener(new ToyVpnConnection.OnEstablishListener() {
            public void onEstablish(ParcelFileDescriptor tunInterface) {
                mHandler.sendEmptyMessage(R.string.connected);
                mConnectingThread.compareAndSet(thread, null);
                setConnection(new Connection(thread, tunInterface));
            }
        });
        thread.start();
    }
    private void setConnectingThread(final Thread thread) {
        final Thread oldThread = mConnectingThread.getAndSet(thread);
        if (oldThread != null) {
            oldThread.interrupt();
        }
    }
    private void setConnection(final Connection connection) {
        final Connection oldConnection = mConnection.getAndSet(connection);
        if (oldConnection != null) {
            try {
                oldConnection.first.interrupt();
                oldConnection.second.close();
            } catch (IOException e) {
                Log.e(TAG, "Closing VPN interface", e);
            }
        }
    }
    private void disconnect() {
        mHandler.sendEmptyMessage(R.string.disconnected);
        setConnectingThread(null);
        setConnection(null);
        stopForeground(true);
    }
    private void updateForegroundNotification(final int message) {
        startForeground(1, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(mConfigureIntent)
                .build());
    }
}