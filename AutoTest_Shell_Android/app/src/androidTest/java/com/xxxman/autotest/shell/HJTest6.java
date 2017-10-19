package com.xxxman.autotest.shell;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by tuzi on 2017/8/9.
 */

@RunWith(AndroidJUnit4.class)
public class HJTest6 extends  HJTest1{

    MyConnection my  = new MyConnection();
    String url = Constant.URL();
    String phone= "";

    public void test1(User user) throws Exception {

        phone = sqlUtil.selectCode();
        if (phone.length()==13){
            phone = phone.substring(1);
        }
        login(user);            //1.登录
        goGuangChang();         //3.进入广场
        goZhiBo2();        //4.进入直播
        recordVideo();
        getSunshine2(user);          //6.领取阳光
        closeZhiBo();       //7.关直播
        quit();         //8.退出
    }
    //进入直播
    public void goZhiBo2() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject new_list = mUIDevice.findObject(new UiSelector().text("频道"));
        new_list.click();
        UiObject zhibozhong = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/type_icon"));
        zhibozhong.click();
    }
    //分享
    public void recordVideo() throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());

        //UiObject share = mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/btn_share"));
        //share.click();
        Thread.sleep(1000);
        mUIDevice.click(2,2);
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(680,1842);
        }else{
            mUIDevice.click(460,1228);
        }
        Thread.sleep(200);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(8000);
        if(is4X){
            mUIDevice.click(540,1700);
        }else{
            mUIDevice.click(360,1228);
        }
        Thread.sleep(5000);
        try{
            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
            send.click();
            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
            end.click();
        }catch (Exception e){
            Thread.sleep(5000);
            try{
                UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                send.click();
                UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                end.click();
            }catch (Exception e1){
                Thread.sleep(5000);
                try{
                    UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                    send.click();
                    UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                    end.click();
                }catch (Exception e2){
                    Thread.sleep(5000);
                    try{
                        UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                        send.click();
                        UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                        end.click();
                    }catch (Exception e3){
                        Thread.sleep(5000);
                        try{
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                            end.click();
                        }catch (Exception e4){
                            Thread.sleep(5000);
                            UiObject send   = mUIDevice.findObject(new UiSelector().text("发布"));
                            send.click();
                            UiObject end = mUIDevice.findObject(new UiSelector().text("完成"));
                            end.click();
                        }
                    }
                }
            }
        }
    }
    public void getSunshine2(User user) throws Exception {
        Log.d(TAG,(log_count++)+":开始方法："+new Exception().getStackTrace()[0].getMethodName()
                +"@上级方法："+new Exception().getStackTrace()[1].getMethodName());
        UiObject sun =mUIDevice.findObject(new UiSelector().resourceId("com.huajiao:id/sun_task_tip"));
        //sun.click();
        Thread.sleep(1000);
        if(is4X){
            mUIDevice.click(976,390);
        }else{
            mUIDevice.click(651,268);
        }
        UiObject2 get = mUIDevice.findObject(By.text("录制视频(1/1)"));
        if(get!=null){
            UiObject2 get_ = get.getParent().findObject(By.text("领取"));
            get_.click();
            sqlUtil.updateSuccessCount(user);
            Log.i(TAG,"count_get_sun:"+count_get_sun);
        }else{
            UiObject2 list = mUIDevice.findObject(By.res("com.huajiao:id/list_view"));
            list.scroll(Direction.DOWN, 0.8f);
            UiObject2 get1 = mUIDevice.findObject(By.text("录制视频(1/1)"));
            UiObject2 end = get1.getParent().findObject(By.text("已完成"));
            if(end != null){
                sqlUtil.updateSuccessCount(user);
                Log.i(TAG,"count_get_sun:"+count_get_sun);
            }
        }
        //记录阳光数
        UiObject2 sun_count = mUIDevice.findObject(By.res("com.huajiao:id/right_tv"));
        if (sun_count != null){
            Log.d(TAG,"---当前阳光数为--"+sun_count.getText()+"---------");
            user.sun = Integer.valueOf(sun_count.getText());
        }
        //完成任务
        String dou = "\"{\\\"phone\\\":\\\""+phone+"\\\",\\\"account\\\":\\\""+user.phone+"\\\",\\\"pwd\\\":\\\"*\\\",\\\"state\\\":\\\"1\\\",\\\"sun\\\":"+user.sun+"}\"";
        String context = "{\"function\":\"F100004\",\"user\":{\"id\":\"1\",\"session\":\"123\"},\"content\":{\"count\":"+dou+"}}";
        Map<String,String> parms = new HashMap<>();
        parms.put("jsonContent",context);
        String rs = my.getContextByHttp(url,parms);
        Log.d(TAG,"http请求结果"+rs);

        mUIDevice.pressBack();
    }
}

