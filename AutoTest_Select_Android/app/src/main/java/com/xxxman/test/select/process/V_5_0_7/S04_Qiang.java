package com.xxxman.test.select.process.V_5_0_7;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import com.xxxman.test.select.Constant;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.sql.TaskSQL;

/**
 * 抢
 * Created by tuzi on 2017/10/22.
 */

public class S04_Qiang {

    private static final String TAG = S04_Qiang.class.getName();

    public static void start(Task task) throws Exception{
        UiDevice mUIDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try{
            mUIDevice.removeWatcher("close_ad");
            boolean is4X = Constant.IS_4X();
            for(int j =0 ;j < 50 ;j++){
                if(is4X){
                    mUIDevice.click(954,1367);
                }else{
                    mUIDevice.click(640,910);
                }
                Thread.sleep(1000);
                UiObject2 hongdou100 = mUIDevice.findObject(By.text("给钱也不要"));
                if(hongdou100!=null) {
                    Log.d(TAG,"红包分享没满~"+task);
                    if (is4X) {
                        mUIDevice.click(990, 1770);
                    } else {
                        mUIDevice.click(660, 1180);
                    }
                    continue;
                }else{
                    Thread.sleep(1000);
                }
                UiObject2 hongbao = mUIDevice.findObject(By.res("android:id/content"));
                if(hongbao==null){
                    Log.d(TAG,"没红包~"+task);
                    //没红包
                    if(j>1){
                        Log.d(TAG,"没红包2~"+task);
                        break;
                    }
                }else{
                    Log.d(TAG,"有红包~"+task);
                    UiObject2 kaihongbao = mUIDevice.findObject(By.res("com.huajiao:id/pre_btn_open"));
                    //情况1：成功
                    if(kaihongbao!=null){
                        Log.d(TAG,"开红包成功~"+task);
                        kaihongbao.click();
                        Thread.sleep(2000);
                        task.setSuccess_count(task.getSuccess_count()+1);
                        TaskSQL.updateTaskCount(task.getId(),"success_count",task.getSuccess_count());
                        break;
                    }else{
                        //情况2：失败
                        UiObject2 wuyuan = mUIDevice.findObject(By.text("和红包无缘相遇，期待下次好运吧~"));
                        if(wuyuan!=null){
                            task.setFail_count(task.getFail_count()+1);
                            Log.d(TAG,"和红包无缘相遇，期待下次好运吧~"+task);
                            TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                            break;
                        }else{
                            //情况3：失败
                            UiObject2 meiyou = mUIDevice.findObject(By.text("没抢到红包，肯定是抢的姿势不对~"));
                            if(meiyou!=null){
                                task.setFail_count(task.getFail_count()+1);
                                Log.d(TAG,"没抢到红包，肯定是抢的姿势不对~"+task);
                                TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                                break;
                            }else{
                                UiObject2 yunqicha = mUIDevice.findObject(By.text("运气不佳，没抢到红包~"));
                                if(yunqicha!=null){
                                    task.setFail_count(task.getFail_count()+1);
                                    Log.d(TAG,"运气不佳，没抢到红包~---更新"+task);
                                    TaskSQL.updateTaskCount(task.getId(),"fail_count",task.getFail_count());
                                    break;
                                }else{
                                    //情况5：未实名
                                    UiObject2 renzheng = mUIDevice.findObject(By.text("实名认证提示"));
                                    if(renzheng!=null){
                                        throw new Exception("未实名认证");
                                    }else {
                                        UiObject2 login = mUIDevice.findObject(By.text("使用手机号登录"));
                                        if(login!=null){
                                            throw new Exception("未登录");
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }finally {
            mUIDevice.runWatchers();
        }

    }
}
