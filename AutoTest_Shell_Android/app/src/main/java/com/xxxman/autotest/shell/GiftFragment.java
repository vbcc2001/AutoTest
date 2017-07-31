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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by tuzi on 2017/7/30.
 */

public class GiftFragment extends Fragment {


    Button runBtn5;
    Button runBtn6;
    Button runBtn7;
    EditText idEdit ;
    EditText huajiaoEdit ;
    EditText perDouEdit ;
    EditText maxDouEdit ;
    TextView douView;
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


        runBtn5 = (Button) view.findViewById(R.id.runBtn5);
        runBtn6 = (Button) view.findViewById(R.id.runBtn6);
        runBtn7 = (Button) view.findViewById(R.id.runBtn7);
        idEdit = (EditText) view.findViewById(R.id.idEdit);
        idEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        huajiaoEdit = (EditText) view.findViewById(R.id.huajiaoEdit);
        huajiaoEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        perDouEdit = (EditText) view.findViewById(R.id.perDouEdit);
        perDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        maxDouEdit = (EditText) view.findViewById(R.id.maxDouEdit);
        maxDouEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        douView =(TextView) view.findViewById(R.id.dou_view);

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
            code= code.substring(0,12);
            Log.d(TAG,code);
            Log.d(TAG,value);
            is_code = code.equals(value);
        } catch (Exception e) {
            e.printStackTrace();
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
                    Toast.makeText(this.getContext(), "没输入任务编号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(huajiao_id_string.isEmpty()){
                    Toast.makeText(this.getContext(), "没输入花椒号!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(per_dou_string.isEmpty()){
                    Toast.makeText(this.getContext(), "没输入每个账号送豆数!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(max_dou_string.trim().isEmpty()){
                    Toast.makeText(this.getContext(), "没输入总送豆数!", Toast.LENGTH_LONG).show();
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
                    if (huajiao_id>0  && per_dou>0 && max_dou>0){
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

    public void getBefore(View v) {
        if(is_code){
            SQLUtil2 sqlUtil2 = new SQLUtil2();
            Order order = sqlUtil2.selectOrder();
            idEdit.setText(""+order.id);
            huajiaoEdit.setText(""+order.huajiao_id);
            perDouEdit.setText(""+order.per_dou);
            maxDouEdit.setText(""+order.max_dou);
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
}