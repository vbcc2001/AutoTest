package com.xxxman.auto.momo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void go_go(View v) {
        Thread new_thread=new MyThead();
        new_thread.start();
        Log.d("asd", "123123");
    }
    private class MyThead  extends Thread{

        private  final String TAG = MyThead.class.getName();
        @Override
        public void run() {
            super.run();
            String path = "com.xxxman.auto.momo";
            String name = "MomoTest";
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
}
