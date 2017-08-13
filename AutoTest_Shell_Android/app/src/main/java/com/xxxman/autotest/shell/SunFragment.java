package com.xxxman.autotest.shell;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by tuzi on 2017/7/30.
 */

public class SunFragment extends Fragment {

    Button runBtn;
    Button runBtn1;
    Button runBtn2;
    TextView rootTextView;
    TextView taskView;
    TextView snView;
    EditText numberEdit;
    EditText numberEditNext;
    SQLUtil sqlUtil = new SQLUtil();
    String code;
    private static final String TAG = SunFragment.class.getName();
    boolean is_code = false;

    public static SunFragment newInstance() {
        SunFragment fragment = new SunFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main,container,false);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle bundle){

        runBtn = (Button) view.findViewById(R.id.runBtn);
        runBtn1 = (Button) view.findViewById(R.id.runBtn1);
        runBtn2 = (Button) view.findViewById(R.id.runBtn2);
        rootTextView = (TextView)  view.findViewById(R.id.root_view);
        taskView =(TextView)  view.findViewById(R.id.task_view);
        snView =  (TextView)  view.findViewById(R.id.sn_view);
        numberEdit = (EditText)  view.findViewById(R.id.number_edit);
        numberEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        numberEditNext = (EditText)  view.findViewById(R.id.number_edit_next);

        //注册程序
        Button fab = (Button) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"程序注册");
                if(is_code) {
                    Toast.makeText(SunFragment.this.getActivity(), "已注册成功！", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent();
                    intent.setClass(SunFragment.this.getActivity(), LoginActivity.class);
                    SunFragment.this.getActivity().startActivity(intent);
                }
            }
        });
        //绑定运行按钮
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runMyUiautomator(view);
            }
        });
        //绑定运行按钮
        runBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runLogin(view);
            }
        });
        //绑定运行按钮
        runBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextLogin(view);
            }
        });
        //创建表
        if (!sqlUtil.tabbleIsExist("count")){
            sqlUtil.createTableCount();
        }
        if (!sqlUtil.tabbleIsExist("code")){
            sqlUtil.createTableCode();
        }
        //读取txt文档，插入到表
        if(sqlUtil.selectUserCount()==0){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt");
                Log.d(TAG,"user_list.txt中用户数量："+list.size());
                sqlUtil.inserCount(list);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this.getActivity(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
            }
        }
        int user_count = 0;
        int task_count = 0;
        int end_task_count = 0;
        //int end_count = 0;
        int success_count = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        user_count =sqlUtil.selectUserCount();
        task_count =sqlUtil.selectTaskCount();
        end_task_count =sqlUtil.selectEndTaskCount();
        success_count =sqlUtil.selectSuccessCount();
        taskView.setText(dateString+"：总任务数"+user_count+"\n待处理数"+task_count+"，已处理"+end_task_count+"个，成功"+success_count+"个!" );

        //上一次登录信息
        List<User> list_login = sqlUtil.selectLoginCount();
        if(list_login.size()>0) {
            numberEditNext.setText(""+list_login.get(0).number);
        }

        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            rootTextView.setText("Root成功，" );
            rootTextView.setTextColor(ContextCompat.getColor(this.getActivity(), R.color.green));
        }
        //判断是否注册
        String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                "ioZotQIDAQAB";
        RSAUtils.loadPublicKey(pubkey);
        String value = sqlUtil.selectCode();
        String sn = SNUtil.getuniqueId(fab.getContext());
        String enctytCode = null;
        try {
            enctytCode = RSAUtils.encryptWithRSA(sn);
            code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            code= code.substring(0,12);
            Log.d(TAG,code);
            Log.d(TAG,value);
            is_code = ("@"+code).equals(value);
            if(value.length()==12){
                is_code = code.equals(value);
            }
            //is_code = code.equals("94e433225479");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(is_code){
            fab.setVisibility(View.GONE);
            snView.setText("注册成功！("+code+")" );
            snView.setTextColor(ContextCompat.getColor(this.getActivity(), R.color.green));
        }
    }
    public void runMyUiautomator(View v) {
        if(is_code){
            if (sqlUtil.selectTaskCount()>0 || sqlUtil.selectFailCount().size()>0) {
                new UiautomatorThread().start();
                Log.i(TAG, "runMyUiautomator: ");
            }else{
                Toast.makeText(this.getActivity(), "无未完成任务可运行！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this.getActivity(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void runLogin(View v) {
        if(is_code){
            String number_string = numberEdit.getText().toString().trim();
            int number = Integer.parseInt(number_string);
            String path = null;
            try {
                path = Environment.getExternalStorageDirectory().getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt",number);
            Log.d(TAG,"登录人数：---"+list.size());
            sqlUtil.inserLoginCount(list);
            if (list.size()>0 && sqlUtil.selectLoginCount().size()>0) {
                new UiautomatorThread2().start();
                Log.i(TAG, "runMyUiautomator: ");
            }else{
                Toast.makeText(this.getActivity(), "未找到该用户！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this.getActivity(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void nextLogin(View v) {
        if(is_code){
            String number_string = numberEditNext.getText().toString().trim();
            int number = Integer.parseInt(number_string);
            String path = null;
            try {
                path = Environment.getExternalStorageDirectory().getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt",number+1);
            Log.d(TAG,"登录人数：---"+list.size());
            sqlUtil.inserLoginCount(list);
            if (list.size()>0 && sqlUtil.selectLoginCount().size()>0) {
                new UiautomatorThread2().start();
                Log.i(TAG, "runMyUiautomator: ");
            }else{
                Toast.makeText(this.getActivity(), "未找到该用户！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this.getActivity(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
     */
    static class UiautomatorThread extends Thread {
        @Override
        public void run() {
            super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest1 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.e("CommandResult", "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
    class UiautomatorThread2 extends Thread {
        @Override
        public void run() {
            super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest2 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
}