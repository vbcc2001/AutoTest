package com.xxxman.autotest.shell;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "LoginActivity";
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
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    ImageView ivTwoCode;
    private TextView snView;
    private SendHandler sendHandler;
    private GetHandler getHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        sendHandler = new SendHandler();
        getHandler = new GetHandler();
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(view);
            }
        });
        Button sendButton = (Button) findViewById(R.id.web_send_in_button);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
        mProgressView = findViewById(R.id.login_progress);

        String sn = SNUtil.getuniqueId(mEmailView.getContext());

        snView = (TextView) findViewById(R.id.sn_view);
        snView.setText(sn);

        mEmailView.setText(sn);
        //mPasswordView.setText("4b592e783869");
        //mPasswordView.setFocusable(true);
        //mPasswordView.requestFocus();
        ivTwoCode= (ImageView) findViewById(R.id.iv1);
        Bitmap bitmap = ZXingUtils.createQRImage(sn, 256, 256);
        ivTwoCode.setImageBitmap(bitmap);
    }
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(View v) {
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
            String sn = SNUtil.getuniqueId(v.getContext());
            //String sign = RSAUtils.sign(sn);
            enctytCode = RSAUtils.encryptWithRSA(sn);
            String code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            //String code = RSAUtils.decryptWithRSA(enctytCode);
            //Log.d(TAG,sign);
            Log.d(TAG,mPasswordView.getText().toString());
            Log.d(TAG,mPasswordView.getText().toString());
            code= code.substring(0,12);
            Log.d(TAG,code);
            if(code.equals(mPasswordView.getText().toString())){
                new SQLUtil().inserCode(mPasswordView.getText().toString());
                Log.d(TAG,"注册码为："+mPasswordView.getText().toString());
                Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "注册失败！请重新注册", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL;
            String sn = SNUtil.getuniqueId(mEmailView.getContext());
            //更新到服务器
            String context = "{\"function\":\"F200000\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"sn\":\"" + sn + "\"}}";
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
        }
    }
    class GetThread extends Thread {
        @Override
        public void run() {
            super.run();
            MyConnection my  = new MyConnection();
            String url = Constant.URL;
            String sn = SNUtil.getuniqueId(mEmailView.getContext());
            //更新到服务器
            String context = "{\"function\":\"F200001\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"sn\":\"" + sn + "\"}}";
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
                if(requestParameter.getErrorNo()=="" && requestParameter.getList().size()>0){
                    b.putString("code",requestParameter.getDataRow().getString("phone"));
                }
//                b.putString("error_num","");
//                b.putString("error_msg","");
//                b.putString("code","123");
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
                Toast.makeText(getApplicationContext(), "发送注册码成功,待通过后,点击获取验证码。", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "发送注册码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "获得验证码成功,请点击注册。", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "获得验证码失败，"+b.getString("error_num")+"："+b.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

