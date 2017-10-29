package com.xxxman.auto.momo.process;

import android.content.Context;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class MomoTest {
    UiDevice mUIDevice = null;
    Context mContext = null;

    @Test

    public void useAppContext() throws Exception {

//            Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.immomo.momo");  //启动app
//            mContext.startActivity(myIntent);
//            mUIDevice.waitForWindowUpdate("com.immomo.momo", 5 * 2000);
//
//        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.immomo.momo:id/tab_item_tv_label"));
//        zhibozhong.click();
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象


//    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//        String t=format.format(new Date());
//        t.
//        Log.e("msg", t);
//        for (int i=0; i < 1000; i++) {
//            Calendar calendar = Calendar.getInstance();
//            int min = calendar.get(Calendar.MINUTE);
//
//            if(min%10==9){
//                int sec = calendar.get(Calendar.SECOND);
//                if (sec>50) {
//                    for (int i =0;i<120;i++) {
//                        mUIDevice.waitForIdle(500);
//                        mUIDevice.click(51, 1673);
//                        break;
//                    }
//                }
//                mUIDevice.waitForIdle(1000);
//            }
//            mUIDevice.waitForIdle(10000);
//        }
        for (int i=0; i < 10000; i++) {
            Calendar calendar = Calendar.getInstance();
            int min = calendar.get(Calendar.MINUTE);
            Log.d("debug","min="+min+":"+calendar.get(Calendar.SECOND));
            if(min%10==9 || min%10 ==0){
                int sec = calendar.get(Calendar.SECOND);
                Log.d("debug","sec="+sec);
                mUIDevice.click(51, 1713);
                UiObject2 hongbao = mUIDevice.findObject(By.res("com.immomo.momo:id/mk_dialog_webview_container"));
                if(hongbao!=null){
                    mUIDevice.click(545, 960);
                    Thread.sleep(4000);
                }
            }
            Thread.sleep(500);
        }
    }

    @Before
    public void setUp() throws RemoteException {

//        //mUIDevice.waitForIdle(3000); //设置等待时间
//        mContext = InstrumentationRegistry.getContext();
//        if(!mUIDevice.isScreenOn()){  //唤醒屏幕
//            mUIDevice.wakeUp();
//        }
//        mUIDevice.pressHome();  //按home键
    }





}
