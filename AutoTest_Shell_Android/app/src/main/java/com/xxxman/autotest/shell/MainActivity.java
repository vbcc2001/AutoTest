package com.xxxman.autotest.shell;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "MainActivity_HJ";
    boolean is_code = false;
    Button runBtn;
    Button getRoot;
    TextView rootTextView;
    TextView taskView;
    TextView hongbaoView;
    TextView snView;
    EditText numberEdit;
    EditText numberEdit1;
    EditText idEdit ;
    EditText huajiaoEdit ;
    EditText perDouEdit ;
    EditText maxDouEdit ;
    SQLUtil sqlUtil = new SQLUtil();
    TabLayout mTablayout;
    ViewPager mViewPager;
    TabTitlePager mTabTitleAdapter;
    String[] titleArr = new String[]{"阳光","红包","礼物"};
    //String[] titleArr = new String[]{"礼物"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTablayout = (TabLayout) findViewById(R.id.tabLayout);
        //mTablayout.addTab(mTablayout.newTab().setText("阳光"));
        //mTablayout.addTab(mTablayout.newTab().setText("红包"));
        //mTablayout.addTab(mTablayout.newTab().setText("礼物"));
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.setTabMode(TabLayout.MODE_FIXED);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabTitleAdapter = new TabTitlePager(getSupportFragmentManager());
        mViewPager.setAdapter(mTabTitleAdapter);
        mTablayout.setupWithViewPager(mViewPager);

        runBtn = (Button) findViewById(R.id.runBtn);
        rootTextView = (TextView) findViewById(R.id.root_view);
        taskView =(TextView) findViewById(R.id.task_view);
        hongbaoView =(TextView) findViewById(R.id.hongbao_view);
        snView =  (TextView) findViewById(R.id.sn_view);
        numberEdit = (EditText) findViewById(R.id.number_edit);
        numberEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        numberEdit1 = (EditText) findViewById(R.id.number_edit1);
        numberEdit1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        idEdit = (EditText) findViewById(R.id.idEdit);
        idEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        huajiaoEdit = (EditText) findViewById(R.id.huajiaoEdit);
        huajiaoEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        perDouEdit = (EditText) findViewById(R.id.perDouEdit);
        perDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        maxDouEdit = (EditText) findViewById(R.id.maxDouEdit);
        maxDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"11111");
                if(is_code) {
                    Toast.makeText(getApplicationContext(), "已注册成功！", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
        if (!sqlUtil.tabbleIsExist("count")){
            sqlUtil.createTableCount();
        }
        if (!sqlUtil.tabbleIsExist("code")){
            sqlUtil.createTableCode();
        }
        //读取txt文档
        if(sqlUtil.selectUserCount()==0){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt");
                Log.d(TAG,"user_list.txt中用户数量："+list.size());
                sqlUtil.inserCount(list);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
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
        task_count = sqlUtil.selectHongbaoTaskCount();
        end_task_count = user_count-task_count;
        success_count = sqlUtil.selectHongbaoSuccessCount();
        hongbaoView.setText(dateString+"：总任务数"+user_count+"\n待处理数"+task_count+"，已处理"+end_task_count+"个，成功"+success_count+"个!");
        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            rootTextView.setText("已经获取Root权限，" );
            rootTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
        }

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
            String code = SNUtil.getMD5(enctytCode);
            code= code.substring(0,12);
            Log.d(TAG,code);
            Log.d(TAG,value);
            is_code = code.equals(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(is_code){
            fab.setVisibility(View.GONE);
            snView.setText("程序注册成功！" );
            snView.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    public void runMyUiautomator(View v) {
        if(is_code){
            if (sqlUtil.selectTaskCount()>0 || sqlUtil.selectFailCount().size()>0) {
                new UiautomatorThread().start();
                Log.i(TAG, "runMyUiautomator: ");
            }else{
                Toast.makeText(getApplicationContext(), "无未完成任务可运行！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "未找到该用户！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void runHongbao(View v) {
        if(is_code){
            if (sqlUtil.selectHongbaoUser().size() >0 || sqlUtil.selectHongbaoFailUser().size()>0) {
                UiautomatorThread3 thread3 = new UiautomatorThread3();
                Log.i(TAG, "runMyUiautomator: ");
                try {
                    Thread.sleep(10000);
                    Log.i(TAG, "runMyUiautomator: 10s----");
                    thread3.suspend();
                    Thread.sleep(10000);
                    Log.i(TAG, "runMyUiautomator: 20s----");
                    thread3.resume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), "无未完成任务可运行！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void runHongbao2(View v) {
        if(is_code){
            String number_string = numberEdit1.getText().toString().trim();
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
                new UiautomatorThread4().start();
                Log.i(TAG, "runMyUiautomator: ");
            }else{
                Toast.makeText(getApplicationContext(), "未找到该用户！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }

    public void songDou(View v) {
        if(is_code){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt");
                SQLUtil2 sqlUtil2 = new SQLUtil2();
                if (!sqlUtil2.tabbleIsExist("dou")){
                    sqlUtil2.createTableDou();
                }
                if (!sqlUtil2.tabbleIsExist("order1")){
                    sqlUtil2.createTableOrder();
                }
                String task_id_string = idEdit.getText().toString().trim();
                String huajiao_id_string = huajiaoEdit.getText().toString().trim();
                String per_dou_string = perDouEdit.getText().toString().trim();
                String max_dou_string = maxDouEdit.getText().toString().trim();
                if(task_id_string.isEmpty()){
                    Toast.makeText(getApplicationContext(), "没输入任务编号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(huajiao_id_string.isEmpty()){
                    Toast.makeText(getApplicationContext(), "没输入花椒号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(per_dou_string.isEmpty()){
                    Toast.makeText(getApplicationContext(), "没输入每个账号送豆数!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(max_dou_string.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "没输入总送豆数!", Toast.LENGTH_LONG).show();
                    return;
                }

                int task_id = Integer.parseInt(task_id_string);
                int huajiao_id = Integer.parseInt(huajiao_id_string);
                int per_dou = Integer.parseInt(per_dou_string);
                int max_dou = Integer.parseInt(max_dou_string);

                Order order = new Order(task_id,huajiao_id,per_dou,max_dou);

                if(task_id<=0){
                    //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
                    //order.id = Integer.parseInt(formatter.format(new Date()));
                    //sqlUtil2.inserHongbao(list,order);
                    Toast.makeText(getApplicationContext(), "任务编号错误!", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    sqlUtil2.inserOrder(order);
                    List<User> list2 = sqlUtil2.selectDouUser(order);
                    if(list2.size()==0){
                        sqlUtil2.inserDou(list,order);
                    }else{
                        list =list2;
                    }
                }
                if (list.size()>0 ) {
                    if (huajiao_id>0  && per_dou>0 && max_dou>0){
                        UiautomatorThread5 thread5 = new UiautomatorThread5();
                        Log.i(TAG, "runMyUiautomator5: ");
                    }else{
                        Toast.makeText(getApplicationContext(), "任务数据输入错误!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "未找到该用户！", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }

    public void getBefore(View v) {
        if(is_code){
            SQLUtil2 sqlUtil2 = new SQLUtil2();
            Order order = sqlUtil2.selectOrder();
            idEdit.setText(""+order.id);
            huajiaoEdit.setText(""+order.huajiao_id);
            perDouEdit.setText(""+order.per_dou);
            maxDouEdit.setText(""+order.max_dou);
            TextView douView =(TextView) findViewById(R.id.dou_view);
            int sum = sqlUtil2.selectSendDou(order.id);
            int user_sum = sqlUtil2.selectDouUser(order).size();
            if(user_sum==0){
                douView.setTextColor(ContextCompat.getColor(this, R.color.green));
            }else{
                douView.setTextColor(ContextCompat.getColor(this, R.color.red));
            }
            douView.setText("上次：已送"+sum+"个，还剩"+(order.max_dou-sum)+"个（"+user_sum+"个账户未送完）");
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void setNull(View v) {
        if(is_code){
            idEdit.setText("");
            huajiaoEdit.setText("");
            perDouEdit.setText("");
            maxDouEdit.setText("");
            TextView douView =(TextView) findViewById(R.id.dou_view);
            douView.setText("");
        }else {
            Toast.makeText(getApplicationContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
     */
    class UiautomatorThread extends Thread {
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
    class UiautomatorThread3 extends BaseThread {

        public UiautomatorThread3() {
            super("HJTest3", false);
        }
        @Override
        public void process() {
            //super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest3 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }

    }
    class UiautomatorThread4 extends Thread {
        @Override
        public void run() {
            super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest4 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
    class UiautomatorThread5 extends BaseThread {
        public UiautomatorThread5() {
            super("HJTest5", false);
        }
        @Override
        public void process() {
            //super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest5 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
    // 获取ROOT权限
    public void get_root(View v) {
            try {
                Toast.makeText(getApplicationContext(), "正在获取ROOT权限...", Toast.LENGTH_LONG).show();
                Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
            }

    }

    public void register(View v){

    }


    public class TabTitlePager extends FragmentPagerAdapter {

        private LayoutInflater mInflater;

        public TabTitlePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PageFragment fragment = new PageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", ""+position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleArr[position];
        }

        public View getTabView(int position){
            mInflater = LayoutInflater.from(MainActivity.this);
            View view = mInflater.inflate(R.layout.content_main, null);
            if (position==1){
                view = mInflater.inflate(R.layout.content_hongbao, null);
            }
            //TextView textView = (TextView) view.findViewById(R.id.title_tv);
            //ImageView img = (ImageView) view.findViewById(R.id.img);
            //img.setImageResource(R.mipmap.ic_launcher);
            //textView.setText(titleArr[position]);
            return  view;
        }

    }
}
