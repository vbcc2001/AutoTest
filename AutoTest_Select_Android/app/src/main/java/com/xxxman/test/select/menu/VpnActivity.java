package com.xxxman.test.select.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xxxman.test.select.R;
import com.xxxman.test.select.service.MyHttpService;
import com.xxxman.test.select.service.MyVpnService;
import com.xxxman.test.select.service.ToyVpnService;

public class VpnActivity extends AppCompatActivity {

    private static final String TAG = VpnActivity.class.getName();
    private  TextView serverAddress ;
    private  TextView serverPort;
    private  TextView sharedSecret;
    private  SharedPreferences prefs;


    public interface Prefs {
        String NAME = "connection";
        String SERVER_ADDRESS = "server.address";
        String SERVER_PORT = "server.port";
        String SHARED_SECRET = "shared.secret";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpn_form);
        serverAddress = (TextView) findViewById(R.id.address);
        serverPort = (TextView) findViewById(R.id.port);
        sharedSecret = (TextView) findViewById(R.id.secret);
        prefs = getSharedPreferences(Prefs.NAME, MODE_PRIVATE);
        serverAddress.setText(prefs.getString(Prefs.SERVER_ADDRESS, ""));
        serverPort.setText(prefs.getString(Prefs.SERVER_PORT, ""));
        sharedSecret.setText(prefs.getString(Prefs.SHARED_SECRET, ""));
    }
    public void connect(View view){
        prefs.edit()
                .putString(Prefs.SERVER_ADDRESS, serverAddress.getText().toString())
                .putString(Prefs.SERVER_PORT, serverPort.getText().toString())
                .putString(Prefs.SHARED_SECRET, sharedSecret.getText().toString())
                .commit();
        Intent intent = VpnService.prepare(VpnActivity.this);
        Log.d(TAG,"==============intent != null:"+(intent != null));
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }
    }
    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            startService(getServiceIntent().setAction(ToyVpnService.ACTION_CONNECT));
        }
    }
    public void disconnect(View view){
        startService(getServiceIntent().setAction(ToyVpnService.ACTION_DISCONNECT));
    }
    private Intent getServiceIntent() {
        return new Intent(this, MyVpnService.class);
        //return new Intent(this, ToyVpnService.class);
    }
}
