package com.xxxman.test.select.util;

import android.util.Log;

import com.xxxman.test.select.fragment.HongbaoFragment;

public class UiautomatorThread extends BaseThread {

    private static final String TAG = UiautomatorThread.class.getName();

    public UiautomatorThread(String className) {
        super(className, false);
    }
    @Override
    public void process(){
        String path = "com.xxxman.test.select";
        String name = getName();
        String command = "am instrument --user 0 -w -r -e debug false -e class " +
                path+".process."+name+" "+path+
                "/android.support.test.runner.AndroidJUnitRunner";
        ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
        Log.d(TAG, "command: " + command );
        Log.d(TAG, "run: " + rs.result );
        Log.d(TAG, "responseMsg: " + rs.responseMsg );
        Log.d(TAG, "errorMsg: "  + rs.errorMsg);
    }

}