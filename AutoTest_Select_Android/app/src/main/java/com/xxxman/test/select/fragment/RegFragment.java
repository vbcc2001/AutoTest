package com.xxxman.test.select.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xxxman.test.select.R;
import com.xxxman.test.select.menu.LoginActivity;
import com.xxxman.test.select.menu.VpnActivity;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.process.V_5_0_7.S00_Get_Sn_Code;
import com.xxxman.test.select.service.MyHttpService;
import com.xxxman.test.select.service.MyVpnService;
import com.xxxman.test.select.service.ToyVpnService;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.RSAUtils;
import com.xxxman.test.select.util.SNUtil;
import com.xxxman.test.select.util.ShellUtil;
import com.xxxman.test.select.util.UiautomatorThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegFragment extends Fragment {

    private static final String TAG = RegFragment.class.getName();
    private boolean is_register = false;
    private  EditText reg_count ;
    private  EditText api_user;
    private  EditText api_pwd;
    private  EditText reg_pwd;
    private  EditText project_id;
    private  SharedPreferences prefs;

    public static RegFragment newInstance() {
        RegFragment fragment = new RegFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_reg,container,false);
        reg_count = (EditText) view.findViewById(R.id.reg_count);
        api_user = (EditText) view.findViewById(R.id.api_user);
        api_pwd = (EditText) view.findViewById(R.id.api_pwd);
        reg_pwd = (EditText) view.findViewById(R.id.reg_pwd);
        project_id = (EditText) view.findViewById(R.id.project_id);
        prefs = this.getActivity().getSharedPreferences("huajiao_reg", this.getActivity().MODE_PRIVATE);
        reg_count.setText(prefs.getString("reg_count", ""));
        api_user.setText(prefs.getString("api_user", ""));
        api_pwd.setText(prefs.getString("api_pwd", ""));
        reg_pwd.setText(prefs.getString("reg_pwd", ""));
        project_id.setText(prefs.getString("project_id", ""));
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle bundle){
        Button regBtn = (Button) view.findViewById(R.id.regBtn);
        //绑定运行按钮
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit()
                        .putString("reg_count", reg_count.getText().toString())
                        .putString("api_user", api_user.getText().toString())
                        .putString("api_pwd", api_pwd.getText().toString())
                        .putString("reg_pwd", reg_pwd.getText().toString())
                        .putString("project_id", project_id.getText().toString())
                        .commit();
                if(is_register && ShellUtil.hasRootPermission() ){
                    UiautomatorThread thread = new UiautomatorThread("V_5_0_7.M02_Register");
                }else{
                    Toast.makeText(RegFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button runBtn1 = (Button) view.findViewById(R.id.runBtn1);
        //注册程序
        Button fab = (Button) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RegFragment.this.getActivity(), LoginActivity.class);
                RegFragment.this.getActivity().startActivity(intent);
            }
        });
        //判断是否Root
        if(ShellUtil.hasRootPermission()){
            TextView rootTextView = (TextView)  view.findViewById(R.id.root_view);
            rootTextView.setText("Root成功，" );
            rootTextView.setTextColor(ContextCompat.getColor(this.getActivity(), R.color.green));
        }
        try {
            //判断是否注册
            String sn = SNUtil.getuniqueId(this.getContext());
            String enctytCode = RSAUtils.encryptWithRSA(sn);
            String code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            code= code.substring(0,12);
            Log.d(TAG,code);
            SharedPreferences preferences= this.getContext().getSharedPreferences("sn_code", Context.MODE_PRIVATE);
            String sn_code =preferences.getString("sn_code", "xxx");
            if(code.equals(sn_code)){
                is_register = true;
                fab.setVisibility(View.GONE);
                TextView snView =  (TextView)  view.findViewById(R.id.sn_view);
                snView.setText("注册成功！("+code+")" );
                snView.setTextColor(ContextCompat.getColor(this.getActivity(), R.color.green));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getActivity(), "注册失败，请检查是否授予获取手机信息的权限", Toast.LENGTH_LONG).show();
        }
    }
}