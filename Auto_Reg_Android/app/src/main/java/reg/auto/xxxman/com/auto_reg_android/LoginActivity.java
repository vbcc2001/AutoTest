package reg.auto.xxxman.com.auto_reg_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView ivTwoCode;
    private TextView snView;
    private static String  TAG ="LoginActivity";
    private SendHandler sendHandler;
    private GetHandler getHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        snView = (TextView) findViewById(R.id.sn_view);
        sendHandler = new SendHandler();
        getHandler = new GetHandler();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                        "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                        "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                        "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                        "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                        "ioZotQIDAQAB";
                RSAUtils.loadPublicKey(pubkey);
//      RSAUtils.loadPrivateKey(priKey);
                String enctytCode = null;
                try {
                    String sn = ""+mEmailView.getText().toString().trim();
                    if("".equals(sn)){
                        Toast.makeText(getApplicationContext(), "注册码不能为空", Toast.LENGTH_LONG).show();
                    }else {
                        //String sign = RSAUtils.sign(sn);
                        enctytCode = RSAUtils.encryptWithRSA(sn);
                        String code = SNUtil.getMD5(enctytCode);
                        code = SNUtil.getMD5(code);
                        code = code.substring(0, 12);
                        snView.setText(code);
                        //mPasswordView.setText("4b592e783869");
                        //mPasswordView.setFocusable(true);
                        ivTwoCode= (ImageView) findViewById(R.id.iv1);
                        Bitmap bitmap = ZXingUtils.createQRImage(code, 256, 256);
                        ivTwoCode.setImageBitmap(bitmap);
                        SendThread thread = new SendThread();
                        thread.start();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Button sendButton = (Button) findViewById(R.id.web_send_in_button);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                snView.setText("不通过");
                SendThread thread = new SendThread();
                thread.start();
            }
        });
        Button getButton = (Button) findViewById(R.id.web_get_in_button);
        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GetThread thread = new GetThread();
                thread.start();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }
    class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL;
            String sn = ""+mEmailView.getText().toString().trim();
            String code = ""+snView.getText().toString().trim();
            if(!"".equals(sn) && !"".equals(code) ){
                //更新到服务器
                String context = "{\"function\":\"F200000\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"sn\":\"" + sn + "\",\"phone\":\"" + code + "\"}}";
                Map<String, String> parms = new HashMap<>();
                parms.put("jsonContent", context);
                Log.d(TAG, "http请求" + context);
                String rs = my.getContextByHttp(url, parms);
                Log.d(TAG, "http请求结果" + rs);
                Gson gson = new GsonBuilder().serializeNulls().create();
                try{
                    HttpResult requestParameter = (HttpResult) gson.fromJson(rs, new TypeToken<HttpResult<Map<String,String>>>() {}.getType());
                    Log.d(TAG, "error" + requestParameter.getErrorNo()+":"+requestParameter.getErrorInfo());
                    Log.d(TAG, "list" + "("+requestParameter.getList().size()+"):"+requestParameter.getList());
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("error_num",requestParameter.getErrorNo());
                    b.putString("error_msg",requestParameter.getErrorInfo());
                    msg.setData(b);
                    LoginActivity.this.sendHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                }catch (Exception e){
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("error_num","-999");
                    b.putString("error_msg","网络错误！");
                    msg.setData(b);
                    LoginActivity.this.sendHandler.sendMessage(msg); // 向Handler发送消息，更新UI
                }
            }else{
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("error_num","-998");
                b.putString("error_msg","注册码和验证码不能为空");
                msg.setData(b);
                LoginActivity.this.sendHandler.sendMessage(msg); // 向Handler发送消息，更新UI
            }

        }
    }
    class GetThread extends Thread {
        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL;
            //更新到服务器
            String context = "{\"function\":\"F200002\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{}}";
            Map<String, String> parms = new HashMap<>();
            parms.put("jsonContent", context);
            Log.d(TAG, "http请求" + context);
            String rs = my.getContextByHttp(url, parms);
            Log.d(TAG, "http请求结果" + rs);
            Gson gson = new GsonBuilder().serializeNulls().create();
            try{
                HttpResult requestParameter = (HttpResult) gson.fromJson(rs, new TypeToken<HttpResult<Map<String,String>>>() {}.getType());
                Log.d(TAG, "error" + requestParameter.getErrorNo()+":"+requestParameter.getErrorInfo());
                Log.d(TAG, "list" + "("+requestParameter.getList().size()+"):"+requestParameter.getList());
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("error_num",requestParameter.getErrorNo());
                b.putString("error_msg",requestParameter.getErrorInfo());
                if(requestParameter.getErrorNo()=="" ){
                    b.putInt("size",requestParameter.getList().size());
                    if(requestParameter.getList().size()>0){
                        b.putString("code",requestParameter.getDataRow().getString("register"));
                    }
                }
                msg.setData(b);
                LoginActivity.this.getHandler.sendMessage(msg); // 向Handler发送消息，更新UI
            }catch (Exception e){
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("error_num","-999");
                b.putString("error_msg","网络错误！");
                msg.setData(b);
                LoginActivity.this.getHandler.sendMessage(msg); // 向Handler发送消息，更新UI
            }
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
                Toast.makeText(getApplicationContext(), "发送验证码成功.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "发送验证码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
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
                int size = b.getInt("size");
                if(size>0){
                    mEmailView.setText(b.getString("code"));
                    Toast.makeText(getApplicationContext(), "获得注册码成功,待注册机器为"+size+",请点击注册。", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "当前没有要注册的机器。", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "获得注册码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        }
    }
}

