package com.xxxman.autotest.shell;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by tuzi on 2017/7/30.
 */

public class GiftFragment extends Fragment {

    Button runBtn3;
    Button runBtn4;
    Button runBtn5;
    Button runBtn6;
    Button runBtn7;
    EditText idEdit ;
    EditText huajiaoEdit_sun ;
    EditText startAccoutEdit_sun;
    EditText hotTimeEdit;
    CheckBox isTalkCheckBox;
    CheckBox isSunCheckBox;
    CheckBox isXrkCheckBox;
    TextView douView;
    EditText huajiaoEdit ;
    EditText perDouEdit ;
    EditText maxDouEdit ;
    EditText startAccoutEdit;
    SQLUtil sqlUtil = new SQLUtil();

    private static final String TAG = GiftFragment.class.getName();
    boolean is_code = false;

    public static GiftFragment newInstance() {
        GiftFragment fragment = new GiftFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_liwu,container,false);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle bundle){

        runBtn3 = (Button) view.findViewById(R.id.runBtn5_sun);
        runBtn4 = (Button) view.findViewById(R.id.runBtn6_sun);
        runBtn5 = (Button) view.findViewById(R.id.runBtn5);
        runBtn6 = (Button) view.findViewById(R.id.runBtn6);
        runBtn7 = (Button) view.findViewById(R.id.runBtn7);
        idEdit = (EditText) view.findViewById(R.id.idEdit);
        idEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        huajiaoEdit = (EditText) view.findViewById(R.id.huajiaoEdit);
        huajiaoEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        huajiaoEdit_sun = (EditText) view.findViewById(R.id.huajiaoEdit_sun);
        huajiaoEdit_sun.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        perDouEdit = (EditText) view.findViewById(R.id.perDouEdit);
        perDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        maxDouEdit = (EditText) view.findViewById(R.id.maxDouEdit);
        maxDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        startAccoutEdit =(EditText) view.findViewById(R.id.startAccoutEdit);
        startAccoutEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        startAccoutEdit_sun =(EditText) view.findViewById(R.id.startAccoutEdit_sun);
        startAccoutEdit_sun.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        hotTimeEdit =(EditText) view.findViewById(R.id.hotTimeEdit);
        hotTimeEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        isTalkCheckBox =(CheckBox) view.findViewById(R.id.isTalkCheckBox);
        isSunCheckBox =(CheckBox) view.findViewById(R.id.sunCheckBox);
        isXrkCheckBox =(CheckBox) view.findViewById(R.id.xrkCheckBox);
        douView =(TextView) view.findViewById(R.id.dou_view);
        runBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songSun (view);
            }
        });
        runBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                before_sun (view);
            }
        });
        //绑定运行按钮
        runBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songDou (view);
            }
        });
        //绑定运行按钮
        runBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBefore(view);
            }
        });
        //绑定运行按钮
        runBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNull(view);
            }
        });
        //判断是否注册
        String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                "ioZotQIDAQAB";
        RSAUtils.loadPublicKey(pubkey);
        String value = sqlUtil.selectCode();
        String sn = SNUtil.getuniqueId(this.getContext());
        String enctytCode = null;
        try {
            enctytCode = RSAUtils.encryptWithRSA(sn);
            String code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            code= code.substring(0,12);
            Log.d(TAG,code);
            Log.d(TAG,value);
            is_code = ("@"+code).equals(value);
            if(value.length()==12){
                is_code = code.equals(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void songDou(View v)  {
        if(is_code){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/bh_NumberList.txt");
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

                String begin_accout_string = startAccoutEdit.getText().toString().trim();
                String wait_time_string = hotTimeEdit.getText().toString().trim();
                if(task_id_string.isEmpty()){
                    Toast.makeText(this.getContext(), "没输入任务编号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(huajiao_id_string.isEmpty()){
                    Toast.makeText(this.getContext(), "没输入花椒号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(max_dou_string.trim().isEmpty()){
                    Toast.makeText(this.getContext(), "没输入总送豆数!", Toast.LENGTH_LONG).show();
                    return;
                }
                int task_id = Integer.parseInt(task_id_string);
                int huajiao_id = Integer.parseInt(huajiao_id_string);

                int max_dou = Integer.parseInt(max_dou_string);

                Order order = new Order();
                order.id =task_id;
                order.huajiao_id =huajiao_id;
                order.max_dou =max_dou;
                if(per_dou_string.isEmpty()){
                    order.per_dou =9999;
                }else{
                    int per_dou = Integer.parseInt(per_dou_string);
                    order.per_dou =per_dou;
                }
                if(begin_accout_string.isEmpty()){
                    order.begin_accout =1;
                }else{
                    int begin_accout = Integer.parseInt(begin_accout_string);
                    order.begin_accout =begin_accout;
                }
                if(!wait_time_string.isEmpty()){
                    int wait_time = Integer.parseInt(wait_time_string);
                    order.wait_time =wait_time;
                }
                order.is_talk = isTalkCheckBox.isChecked();
                Log.d(TAG,"1任务信息为："+order.id+","+order.huajiao_id+","+order.per_dou+","+order.max_dou+","+order.wait_time+","+order.begin_accout+","+order.is_talk);
                if(task_id<=0){
                    //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
                    //order.id = Integer.parseInt(formatter.format(new Date()));
                    //sqlUtil2.inserHongbao(list,order);
                    Toast.makeText(this.getContext(), "任务编号错误!", Toast.LENGTH_LONG).show();
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
                    if (huajiao_id>0  && max_dou>0){
                        UiautomatorThread5 thread5 = new UiautomatorThread5();
                        Log.i(TAG, "runMyUiautomator5: ");
                    }else{
                        Toast.makeText(this.getContext(), "任务数据输入错误!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this.getContext(), "未找到该用户！", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this.getContext(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this.getContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }

    public void songSun(View v)  {
        if(is_code){
            try {
                String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                List<User> list = FileUtil.ReadTxtFile(path+"/NumberList.txt");
                SQLUtil9 sqlUtil9 = new SQLUtil9();
                if (!sqlUtil9.tabbleIsExist("sun")){
                    sqlUtil9.createTableSun();
                }
                if (!sqlUtil9.tabbleIsExist("order_sun")){
                    sqlUtil9.createTableOrder();
                }
                String huajiao_id_string = huajiaoEdit_sun.getText().toString().trim();
                String begin_accout_string = startAccoutEdit_sun.getText().toString().trim();
                boolean is_sun = isSunCheckBox.isChecked();
                if(huajiao_id_string.isEmpty()){
                    Toast.makeText(this.getContext(), "没输入花椒号!", Toast.LENGTH_LONG).show();
                    return;
                }
                Order order = new Order();
                int huajiao_id = Integer.parseInt(huajiao_id_string);
                order.id =huajiao_id;
                order.huajiao_id =huajiao_id;
                order.max_dou =999999;
                order.is_sun = isSunCheckBox.isChecked();
                order.is_xrk = isXrkCheckBox.isChecked();
                if(begin_accout_string.isEmpty()){
                    order.begin_accout =1;
                }else{
                    int begin_accout = Integer.parseInt(begin_accout_string);
                    order.begin_accout =begin_accout;
                }
                sqlUtil9.inserOrder(order);
                List<User> list2 = sqlUtil9.selectSunUser(order);
                if(list2.size()==0){
                    sqlUtil9.inserSun(list,order);
                }
                if (list.size()>0 ) {
                    if (huajiao_id>0 ){
                        UiautomatorThread9 thread9 = new UiautomatorThread9();
                        Log.i(TAG, "runMyUiautomator9: ");
                    }else{
                        Toast.makeText(this.getContext(), "任务数据输入错误!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this.getContext(), "未找到该用户！", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this.getContext(), "读取用户文件出错！", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this.getContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void before_sun(View v) {
        if(is_code){
            SQLUtil9 sqlUtil9 = new SQLUtil9();
            Order order = sqlUtil9.selectOrder();
            huajiaoEdit_sun.setText(""+order.huajiao_id);
            if(order.begin_accout!=1){
                startAccoutEdit_sun.setText(""+order.begin_accout);
            }
        }else {
            Toast.makeText(this.getContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }
    public void getBefore(View v) {
        if(is_code){
            SQLUtil2 sqlUtil2 = new SQLUtil2();
            Order order = sqlUtil2.selectOrder();
            idEdit.setText(""+order.id);
            huajiaoEdit.setText(""+order.huajiao_id);
            maxDouEdit.setText(""+order.max_dou);
            if(order.per_dou!=9999){
                perDouEdit.setText(""+order.per_dou);
            }
            if(order.begin_accout!=1){
                startAccoutEdit.setText(""+order.begin_accout);
            }
            if(order.wait_time!=0){
                hotTimeEdit.setText(""+order.wait_time);
            }
            isTalkCheckBox.setChecked(order.is_talk);

            int sum = sqlUtil2.selectSendDou(order.id);
            int user_sum = sqlUtil2.selectDouUser(order).size();
            if(user_sum==0){
                douView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.green));
            }else{
                douView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.red));
            }
            douView.setText("上次：已送"+sum+"个，还剩"+(order.max_dou-sum)+"个（"+user_sum+"个账户未送完）");
        }else {
            Toast.makeText(this.getContext(), "请先注册！", Toast.LENGTH_LONG).show();
        }
    }

    public void setNull(View v) {
        if(is_code){
            idEdit.setText("");
            huajiaoEdit.setText("");
            perDouEdit.setText("");
            maxDouEdit.setText("");
            douView.setText("");
            startAccoutEdit.setText("");
            hotTimeEdit.setText("");
            isTalkCheckBox.setChecked(false);
            douView.setText("");
        }else {
            Toast.makeText(this.getContext(), "请先注册！", Toast.LENGTH_LONG).show();
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
    class UiautomatorThread9 extends BaseThread {
        public UiautomatorThread9() {
            super("HJTest9", false);
        }
        @Override
        public void process() {
            //super.run();
            String command = "am instrument --user 0 -w -r -e debug false -e class " +
                    "com.xxxman.autotest.shell.HJTest9 com.xxxman.autotest.shell.test/android.support.test.runner.AndroidJUnitRunner";
            ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
            Log.i(TAG, "run: " + rs.result + "-------" + rs.responseMsg + "-------" + rs.errorMsg);
        }
    }
}