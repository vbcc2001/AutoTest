package com.xxxman.test.select.process.hehuoren;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.process.V_5_0_7.S00_AD_Close;
import com.xxxman.test.select.util.FileUtil;

import org.junit.Test;

import java.util.List;

/**
 * Created by tuzi on 2017/10/29.
 */

public class M01_Main {
    private static final String TAG = M01_Main.class.getName();
    @Test
    public void test() {
        try{
            UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            Context mContext = InstrumentationRegistry.getContext();
            if(!mUIDevice.isScreenOn()){  //唤醒屏幕
                mUIDevice.wakeUp();
            }
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.thinkerjet.convenient");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
            mUIDevice.waitForWindowUpdate("com.thinkerjet.convenient", 5 * 2000);
            UiObject2 yewu = mUIDevice.findObject(By.text("校园数据业务与存费赠费"));
            if(yewu!=null){
             yewu.click();
            }
            List<Task> list = FileUtil.ReadTxtFile("bh_NumberList.txt");
            for(Task task:list){
                Log.d(TAG,"当然任务手机号："+task.getPhone());
                //UiObject2 dianhua = mUIDevice.findObject(By.res("com.thinkerjet.convenient:id/edtTxt_number_input"));
                //UiObject2 dianhua = mUIDevice.findObject(By.text("手机号码"));
                UiObject dianhua = mUIDevice.findObject(new UiSelector().resourceId("com.thinkerjet.convenient:id/edtTxt_number_input"));
                dianhua.setText(task.getPhone());

                UiObject2 aiqiyi = mUIDevice.findObject(By.text("爱奇艺定向流量包免费送3个月"));
                if(aiqiyi!=null){
                    aiqiyi.click();
                }
                Thread.sleep(5000);
                UiObject tijiao = mUIDevice.findObject(new UiSelector().resourceId("com.thinkerjet.convenient:id/fab"));
                tijiao.click();
                mUIDevice.pressBack();
                //break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
