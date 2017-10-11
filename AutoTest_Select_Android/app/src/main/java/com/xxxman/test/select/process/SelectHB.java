package com.xxxman.test.select.process;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.util.UiautomatorThread;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SelectHB {
    private static final String TAG = SelectHB.class.getName();
    String APP = "com.huajiao";
    int log_count = 0;
    UiDevice mUIDevice = null;
    Context mContext = null;
    String citys[] = new String[]{"最新","深圳","北京","上海","广州","黑龙江","吉林","辽宁","浙江"};
    int next_city = 0;
    @Test
    public void test() {
        mUIDevice.pressHome();  //按home键
        try {
            Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(APP);  //启动app
            mContext.startActivity(myIntent);
            mUIDevice.waitForWindowUpdate(APP, 5 * 2000);
            mUIDevice.pressBack();
            Thread.sleep(3000);
            mUIDevice.pressBack();
            for (int i = 0; i < 9999; i++) {
                try {
                    selectCity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void selectCity() throws Exception{
        if(next_city>=citys.length){
            next_city = 0;
        }
        next_city++;
        selectCityHSM3(citys[next_city-1]);
    }
    public void selectCityHSM3(String city) throws Exception{
        Log.d(TAG,"----------进行城市--------："+city);
        if("最新".equals(city)){
            find_money(city);
        }else{
            UiObject2 city_ui = mUIDevice.findObject(By.text(city));
            if (city_ui==null){
                for(int i=1;i<citys.length;i++){
                    Log.d(TAG,"-------查看城市----------"+citys[i]);
                    city_ui = mUIDevice.findObject(By.text(citys[i]));
                    if (city_ui!=null){
                        if(!city_ui.isSelected()){
                            city_ui.click();
                            Thread.sleep(1000);
                        }
                        Thread.sleep(1000);
                        city_ui.click();
                        if(Constant.LO_CITY.equals(city)){
                            city =city +" (当前定位地区)";
                        }
                        city_ui = mUIDevice.findObject(By.text(city));
                        if (city_ui==null){
                            UiScrollable home = new UiScrollable(new UiSelector().resourceId("com.huajiao:id/hot_area_list"));
                            home.scrollToBeginning(1);
                            city_ui = mUIDevice.findObject(By.text(city));
                            if (city_ui==null){
                                home.scrollToEnd(1);
                            }
                            break;
                        }else{
                            break;
                        }
                    }
                }
                find_money(city);
            }else{
                find_money(city);
            }
        }
    }
    public void find_money(String menu) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject new_list = mUIDevice.findObject(new UiSelector().text(menu));
        new_list.click();
        Thread.sleep(1000);
        int c = 18;
        if(!menu.equals("最新")){
            c = 12;
        }
        for(int i =0 ;i < c ;i++){
                UiObject list = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/listview"));
                list.swipeUp(10);
        }
    }
    @Before
    public void setUp() throws RemoteException {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());  //获得device对象
        mUIDevice.registerWatcher("notifation", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                // just press back
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
    //关闭弹窗广告
    public void closeAd() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName());
        Log.d(TAG,"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject2 close = mUIDevice.findObject(By.res("com.huajiao:id/img_close"));
        if(close!=null){
            Log.d(TAG,"有广告");
            close.click();
        }
    }
}
