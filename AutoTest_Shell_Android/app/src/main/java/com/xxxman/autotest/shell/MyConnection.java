package com.xxxman.autotest.shell;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Drizzt on 2017-07-27.
 */

public class MyConnection implements ServiceConnection {

    private boolean flag=false;//是否绑定服务的标志位
    private MyService.MyBinder localBinder;//服务中的对象
    private MyService myService;//声明一个MyService对象
    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        // TODO Auto-generated method stub
        flag=false;
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODO Auto-generated method stub
        localBinder=(MyService.MyBinder)service;
        myService=localBinder.getService();
        flag=true;
    }
}
