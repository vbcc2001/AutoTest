package com.xxxman.test.select.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xxxman.test.select.R;
import com.xxxman.test.select.menu.LoginActivity;
import com.xxxman.test.select.util.RSAUtils;
import com.xxxman.test.select.util.SNUtil;
import com.xxxman.test.select.util.ShellUtil;
import com.xxxman.test.select.util.ToastUitl;
import com.xxxman.test.select.util.UiautomatorThread;


public class HongbaoFragment extends Fragment {

    private static final String TAG = HongbaoFragment.class.getName();
    private boolean is_register = false;
    public static HongbaoFragment newInstance() {
        HongbaoFragment fragment = new HongbaoFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_hongbao,container,false);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle bundle){
        Button runBtn = (Button) view.findViewById(R.id.runBtn);
        //绑定运行按钮
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(is_register && ShellUtil.hasRootPermission()){
                UiautomatorThread thread = new UiautomatorThread("SelectHB");
            }else{
                Toast.makeText(HongbaoFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
            }
            }
        });
        Button runBtn1 = (Button) view.findViewById(R.id.runBtn1);
        //绑定运行按钮
        runBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(is_register && ShellUtil.hasRootPermission() ){
                UiautomatorThread thread = new UiautomatorThread("ClickHB");
            }else{
                Toast.makeText(HongbaoFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
            }
            }
        });
        //注册程序
        Button fab = (Button) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HongbaoFragment.this.getActivity(), LoginActivity.class);
                HongbaoFragment.this.getActivity().startActivity(intent);
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