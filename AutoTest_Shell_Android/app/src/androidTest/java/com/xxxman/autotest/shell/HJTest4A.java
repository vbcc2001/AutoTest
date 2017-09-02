package com.xxxman.autotest.shell;

import android.content.Intent;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class HJTest4A extends HJTest3{

    String TAG = "HJTest4";
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("notifation", new UiWatcher() {

            @Override
            public boolean checkForCondition() {
                Log.d(TAG,":进入Watcher");
                try {
                    closeAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mContext = InstrumentationRegistry.getContext();
        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
            mUIDevice.wakeUp();
        }
    }
    @Test
    public void test(){

        Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
        mContext.startActivity(myIntent);
        mUIDevice.waitForWindowUpdate(APP, 5 * 2000);


        try {
            String path = Environment.getExternalStorageDirectory().getCanonicalPath();
            List<User> list = FileUtil.ReadTxtFile(path+"/bh_NumberList.txt");

            if(list.size()>0) {
                for(User user:list){
                    login(user);
                    quit(user);
                    MyConnection my  = new MyConnection();
                    String url = Constant.URL;
                    Map<String,String> parms = new HashMap<>();
                    String phone = sqlUtil.selectCode();
                    if (phone.length()==13){
                        phone = phone.substring(1);
                    }
                    //完成任务
                    String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"dou\\\":"+user.dou+"}\"";
                    String context = "{\"function\":\"F100005\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";
                    parms.put("jsonContent",context);
                    String rs = my.getContextByHttp(url,parms);
                    Log.d(TAG,"http请求结果"+rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reboot();
        }
    }
}

