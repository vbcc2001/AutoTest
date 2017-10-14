package com.xxxman.test.select.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxxman.test.select.R;
import com.xxxman.test.select.util.BaseThread;
import com.xxxman.test.select.util.RSAUtils;
import com.xxxman.test.select.util.SNUtil;
import com.xxxman.test.select.util.ShellUtil;
import com.xxxman.test.select.util.UiautomatorThread;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static android.R.attr.name;

public class HongbaoFragment extends Fragment {

    private static final String TAG = HongbaoFragment.class.getName();

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
                UiautomatorThread thread = new UiautomatorThread("SelectHB");
            }
        });
        Button runBtn1 = (Button) view.findViewById(R.id.runBtn1);
        //绑定运行按钮
        runBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiautomatorThread thread = new UiautomatorThread("ClickHB");
            }
        });

        try {
            //判断是否注册
            String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                    "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                    "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                    "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                    "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                    "ioZotQIDAQAB";
            RSAUtils.loadPublicKey(pubkey);
            String sn = SNUtil.getuniqueId(this.getContext());
            String enctytCode = RSAUtils.encryptWithRSA(sn);
            String code = null;
            code = SNUtil.getMD5(enctytCode);
            code = SNUtil.getMD5(code);
            code= code.substring(0,12);
            Log.d(TAG,code);

            SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("sn_code", this.getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sn_code", code);//目前是保存在内存中，还没有保存到文件中
            editor.commit();    //数据提交到xml文件中

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}