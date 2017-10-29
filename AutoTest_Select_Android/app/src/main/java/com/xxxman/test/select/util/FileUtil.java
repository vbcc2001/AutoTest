package com.xxxman.test.select.util;

import android.os.Environment;
import android.util.Log;

import com.xxxman.test.select.object.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tuzi on 2017/7/12.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();


    public static void write(String name,String text) {


        try {
            //生成文件夹之后，再生成文件，不然会出错
            File file = null;
            file = new File(getUserPath());
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(getUserPath() +"/"+ name);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String dateString = formatter.format(new Date());
            String content = text+"\r\n";
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //读取文本文件中的内容
    public static List<Task> ReadTxtFile(String strFilePath) {
        String path = getUserPath()+"/"+strFilePath;
        String content = ""; //文件内容字符串
        List<Task> list = new ArrayList<Task>();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    int i = 1;
                    while (( line = buffreader.readLine()) != null && line.trim().length() > 0 ) {
                        String[] strArray = null;
                        strArray = line.split(",");
                        Task task = new Task();
                        task.setNumber(i);
                        task.setPhone(strArray[0]);
                        task.setPwd(strArray[1]);
                        list.add(task);
                        i++;
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        Log.d("TestFile", "文件读取数量："+list.size());
        return list;
    }

    public static String  getUserPath() {
        String filePath = null;
        try {
             filePath = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
