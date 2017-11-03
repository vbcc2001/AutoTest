package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.util.ToastUitl;
import com.xxxman.test.select.util.XingjkAPI;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by tuzi on 2017/11/1.
 */

public class M02_Register {

    private static final String TAG = M02_Register.class.getName();
    UiDevice mUIDevice = null;
    Context mContext = null;
    @Test
    public void test() {
        try {
            SharedPreferences preferences= mContext.getSharedPreferences("huajiao_reg", Context.MODE_PRIVATE);
            int reg_count =Integer.valueOf(preferences.getString("reg_count", "0"));
            Log.d(TAG,"99999999----"+reg_count);
            String api_user =preferences.getString("api_user", "");
            String api_pwd =preferences.getString("api_pwd", "");
            String reg_pwd =preferences.getString("reg_pwd", "");
            String project_id =preferences.getString("project_id", "");
            //启动APP
            S00_App_Reboot.start();
            Thread.sleep(3000);
            String token = null;
            int suc = 0;
            for(int i = 1 ;i<=reg_count*3;i++){
                if (suc>=reg_count){
                    break;
                }
                try {
                    if(token==null){
                        token = XingjkAPI.loginIn(api_user,api_pwd);
                    }
                    if(token!=null){
                        ToastUitl.sendBroadcast(mContext,"正执行第"+i+"次注册，已成功"+suc+"个");
                        if(S01_Register.start(reg_pwd,project_id,token)){
                          suc++;
                        }
                        S01_Quit_2.start();
                        S00_App_Reboot.start();
                        S00_App_Reboot.start();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUitl.sendBroadcast(mContext,"注册出错："+e.getMessage());
                    S00_App_Reboot.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Before
    public void before() throws RemoteException {
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("close_ad", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                // just press back
                Log.d(TAG,":进入Watcher-close_ad");
                boolean flag = false;
                try {
                    flag = S00_AD_Close.start();
                    if(!flag){
                        flag = S00_Update_Close.start();
                    }
                } catch (Exception e) {
                    Log.d(TAG,":关闭广告失败");
                    e.printStackTrace();
                }
                return flag;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
}
