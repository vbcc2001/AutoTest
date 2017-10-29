package com.xxxman.test.select.process.V_5_0_7;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import com.xxxman.test.select.object.DataRow;
import com.xxxman.test.select.object.HttpResult;
import com.xxxman.test.select.object.Task;
import com.xxxman.test.select.sql.TaskSQL;
import com.xxxman.test.select.util.FileUtil;
import com.xxxman.test.select.util.HttpUtil;
import com.xxxman.test.select.util.SQLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取任务
 * Created by tuzi on 2017/10/22.
 */

public class S00_Get_Task {

    private static final String TAG = S00_Get_Task.class.getName();

    public static List<Task>  start() throws InterruptedException {
        String taskType = "hongbao";
        //创建任务表
        if(!SQLUtil.tabbleIsExist(TaskSQL.table)){
            TaskSQL.createTableTask();
        }
        if(TaskSQL.selectTaskCount(taskType)==0){
            List<Task> list = FileUtil.ReadTxtFile("bh_NumberList.txt");
            for(Task task : list){
                task.setUid(0);
                task.setDay("");
                task.setTask_count(0);
                task.setSuccess_count(0);
                task.setType(taskType);
            }
            TaskSQL.inserTask(list,taskType);
            /*
            for (int i = 0; i < 10; i++) {
                String sn_code = S00_Get_Sn_Code.getCode();
                Map<String,String> para = new HashMap<>();
                para.put("phone",sn_code);
                para.put("type","hongbao");
                HttpResult httpResult = HttpUtil.post("F100010",para);
                if("".equals(httpResult.getErrorNo())) {
                    List<Task> list = new ArrayList<Task>();
                    List<DataRow> list_dataRow =  httpResult.getList();
                    for(DataRow dataRow : list_dataRow){
                        Task task = new Task();
                        task.setNumber(dataRow.getInt("number"));
                        task.setUid(0);
                        task.setPhone(dataRow.getString("accout"));
                        task.setPwd("qaz147258..");
                        if("86d9478e0a89".equals(sn_code) || "c4c8ba9f4fd2".equals(sn_code) || "dfee4b7043cb".equals(sn_code)||"xxx".equals(sn_code)){
                            task.setPwd("x12345678");
                        }
                        task.setDay("");
                        task.setTask_count(0);
                        task.setSuccess_count(0);
                        task.setType(taskType);
                        list.add(task);
                    }
                    TaskSQL.inserTask(list,taskType);
                    break;
                }else{
                    Log.e(TAG,"获取用户信息失败："+httpResult.getErrorInfo());
                    Thread.sleep(5000);
                }
            }        */
        }

        return TaskSQL.selectTask(taskType);
    }
}
