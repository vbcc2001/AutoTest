package com.xxxman.test.select.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.MainActivity;
import com.xxxman.test.select.R;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.util.BaseThread;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.RSAUtils;
import com.xxxman.test.select.util.SNUtil;
import com.xxxman.test.select.util.ToastUitl;
import com.xxxman.test.select.util.ZXingUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private String register_code = null ;
    private SendHandler sendHandler = new SendHandler();
    private GetHandler getHandler = new GetHandler();
    private EditText mPasswordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPasswordView = (EditText) findViewById(R.id.password);

        //显示机器注册码
        try{
            register_code = SNUtil.getuniqueId(this);
            EditText mEmailView = (EditText) findViewById(R.id.email);
            mEmailView.setText(register_code);
            ImageView ivTwoCode= (ImageView) findViewById(R.id.iv1);
            Bitmap bitmap = ZXingUtils.createQRImage(register_code, 256, 256);
            ivTwoCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取机器注册码，请检查是否授予获取手机信息的权限", Toast.LENGTH_LONG).show();
        }
    }
    public void getCode(View view){
        if(register_code==null){
            Toast.makeText(this, "机器注册码不能为空", Toast.LENGTH_LONG).show();
        }else{
            //发送注册码
            BaseThread baseThread = new BaseThread("GetCode",false) {
                @Override
                public void process() {

                    Map<String,String> para = new HashMap<>();
                    para.put("sn",register_code);
                    HttpResult httpResult = HttpUtil.post(Constant.REG_URL,"F200001",para);
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("error_num",httpResult.getErrorNo());
                    b.putString("error_msg",httpResult.getErrorInfo());
                    if(httpResult.getErrorNo()=="" && httpResult.getList().size()>0){
                        b.putString("code",httpResult.getDataRow().getString("phone"));
                    }
                    msg.setData(b);
                    LoginActivity.this.getHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                    this.stop();
                }
            };
        }
    }
    public void sendCode(View view){
        if(register_code==null){
            Toast.makeText(this, "机器注册码不能为空", Toast.LENGTH_LONG).show();
        }else {
            //获取验证码
            BaseThread baseThread = new BaseThread("SendCode", false) {
                @Override
                public void process() {
                    Map<String, String> para = new HashMap<>();
                    para.put("sn", register_code);
                    para.put("tag", Constant.TAG);
                    HttpResult httpResult = HttpUtil.post(Constant.REG_URL, "F200000", para);
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("error_num", httpResult.getErrorNo());
                    b.putString("error_msg", httpResult.getErrorInfo());
                    msg.setData(b);
                    LoginActivity.this.sendHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                    this.stop();
                }
            };
        }
    }
    public void signCode(View view){
        try {
            String enctytCode = RSAUtils.encryptWithRSA(register_code);
            String code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            Log.d(TAG,"获取的注册码为："+mPasswordView.getText().toString());
            code= code.substring(0,12);
            Log.d(TAG,"内部注册码为"+code);
            if(code.equals(mPasswordView.getText().toString())){
                SharedPreferences sharedPreferences = this.getSharedPreferences("sn_code", this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sn_code", code);//目前是保存在内存中，还没有保存到文件中
                editor.commit();    //数据提交到xml文件中
                Toast.makeText(this, "注册成功！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
            }else{
                Toast.makeText(this, "注册失败！请重新注册", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"注册失败，请检查是否授予获取手机信息的权限",Toast.LENGTH_LONG).show();;
        }
    }
    /**
     * 接受消息，处理消息 ，此Handler会与当前主线程一块运行
     * */
    class SendHandler extends Handler {
        public SendHandler() {}
        public SendHandler(Looper L) {
            super(L);
        }
        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            if(b.getString("error_num")==""){
                Toast.makeText(LoginActivity.this, "发送注册码成功,待通过后,点击获取验证码。", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(LoginActivity.this, "发送注册码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        }
    }
    class GetHandler extends Handler {
        public GetHandler() {}
        public GetHandler(Looper L) {
            super(L);
        }
        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            if(b.getString("error_num")==""){
                mPasswordView.setText(b.getString("code"));
                Toast.makeText(LoginActivity.this, "获得验证码成功,请点击注册。", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(LoginActivity.this, "获得验证码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        }
    }
}

