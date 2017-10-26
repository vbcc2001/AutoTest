package com.xxxman.test.select.util;

import android.util.Log;

public class ServiceThread extends BaseThread {

    private static final String TAG = ServiceThread.class.getName();

    public ServiceThread(String className) {
        super(className, false);
    }
    @Override
    public void process(){
        String path = "com.xxxman.test.select";
        String name = getName();
        String command = "am startservice --user 0 -n " +
                path+ "/"+path+".service."+name;
        command = "iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8089";
        ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
        Log.d(TAG, "command: " + command );
        Log.d(TAG, "run: " + rs.result );
        Log.d(TAG, "responseMsg: " + rs.responseMsg );
        Log.d(TAG, "errorMsg: "  + rs.errorMsg);
    }

}