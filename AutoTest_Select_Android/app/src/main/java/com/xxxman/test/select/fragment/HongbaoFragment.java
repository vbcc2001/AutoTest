package com.xxxman.test.select.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xxxman.test.select.Constant;
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
import com.xxxman.test.select.util.ServiceThread;
import com.xxxman.test.select.util.ShellUtil;
import com.xxxman.test.select.util.ToastUitl;
import com.xxxman.test.select.util.UiautomatorThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                //将本机访问80端口的转发到本机8089
                //String command = "iptables -t nat -A OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j DNAT --to 127.0.0.1:8089";
                //String command = "echo \"1\" > /proc/sys/net/ipv4/ip_forward";
                String command = "sysctl -w net.ipv4.ip_forward=1";
                ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                command = "iptables -F";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                command = "iptables -t nat -F";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                command = "iptables -X";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                command = "iptables -P FORWARD ACCEPT";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);

                //String command = "iptables -t nat -A PREROUTING --dst 192.168.3.133 -p tcp --dport 3001 -j DNAT --to-destination 39.108.120.224:3001";
                command = "iptables -t nat -I PREROUTING -p tcp --dport 3001 -j DNAT --to 39.108.120.224";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                //command = "iptables -t nat -A POSTROUTING --dst 39.108.120.224 -p tcp --dport 3001 -j SNAT --to-source 192.168.3.133";
                command = "iptables -t nat -I POSTROUTING -p tcp --dport 3001 -j MASQUERADE";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
                command = "cat /proc/sys/net/ipv4/ip_forward";
                rs = ShellUtil.execCommand(command, true);
                Log.d(TAG, "command: " + command );
                Log.d(TAG, "run: " + rs.result );
                Log.d(TAG, "responseMsg: " + rs.responseMsg );
                Log.d(TAG, "errorMsg: "  + rs.errorMsg);
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
        Button runBtn2 = (Button) view.findViewById(R.id.runBtn2);
        //绑定运行按钮
        runBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_register && ShellUtil.hasRootPermission() ){
                    //将外网访问80端口的数据转发到8089端口
                    String command = "iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8089";
                    ShellUtil.CommandResult rs = ShellUtil.execCommand(command, true);
                    Log.d(TAG, "command 运行结果: " + rs );
                    //将本机访问80端口的转发到本机8089
                    command = "iptables -t nat -A OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j DNAT --to 127.0.0.1:8089";
                    rs = ShellUtil.execCommand(command, true);
                    Log.d(TAG, "command 运行结果: " + rs );
                    // 开启httpServer
                    Intent intent1 = new Intent(HongbaoFragment.this.getActivity(), MyHttpService.class);
                    HongbaoFragment.this.getActivity().startService(intent1);
                    UiautomatorThread thread = new UiautomatorThread("V_5_0_7.M01_QiangHB");
                }else{
                    Toast.makeText(HongbaoFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
                }
            }
        });
        //VPN程序
        Button fab1 = (Button) view.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_register && ShellUtil.hasRootPermission() ){
                    // 开启httpServer
                    Intent intent1 = new Intent(HongbaoFragment.this.getActivity(), MyHttpService.class);
                    HongbaoFragment.this.getActivity().startService(intent1);
                }else{
                    Toast.makeText(HongbaoFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
                }
            }
        });
        //VPN程序
        Button fab2 = (Button) view.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_register && ShellUtil.hasRootPermission() ){
                    // 开启VPN
                    connect(view);
                }else{
                    Toast.makeText(HongbaoFragment.this.getActivity(), "请先注册并赋Root权限", Toast.LENGTH_LONG).show();
                }
            }
        });
        //更新账号
        Button fab3 = (Button) view.findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_register && ShellUtil.hasRootPermission() ){
                    // 更新账号
                    UpdateThread thread = new UpdateThread();
                    thread.start();
                    Toast.makeText(HongbaoFragment.this.getActivity(), "更新成功", Toast.LENGTH_LONG).show();
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
    public void connect(View view){

        Intent intent = VpnService.prepare(this.getActivity());
        Log.d(TAG,"==============intent != null:"+(intent != null));
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, this.getActivity().RESULT_OK, null);
        }
    }
    public void onActivityResult(int request, int result, Intent data) {
        if (result == this.getActivity().RESULT_OK) {
            this.getActivity().startService(getServiceIntent().setAction(ToyVpnService.ACTION_CONNECT));
        }
    }
    private Intent getServiceIntent() {
        return new Intent(this.getActivity(), MyVpnService.class);
    }

    class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                List<Task> list = FileUtil.ReadTxtFile("bh_NumberList.txt");
                Log.d(TAG,"bh_NumberList.txt中用户数量："+list.size());

                Map<String,String> map = new HashMap<>();

                //更新到服务器
                String listStr = "[";
                for (Task task : list) {
                    listStr = listStr + "{n:" + task.getNumber() + ",a:" + task.getPhone() + "},";
                }
                listStr = listStr.substring(0,listStr.length()-1);
                listStr = listStr + "]";
                map.put("phone", S00_Get_Sn_Code.getCode(HongbaoFragment.this.getContext()));
                map.put("list",listStr);
                Log.d(TAG,"更新到服务器中，参数："+map);
                HttpResult httpResult = HttpUtil.post("F100009",map);
                Log.d(TAG,"更新完成，结果："+httpResult.getErrorNo());
                Log.d(TAG,"更新完成，结果："+httpResult.getErrorInfo());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}