package com.xxxman.autotest.shell;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tuzi on 2017/7/12.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    //读取文本文件中的内容
    public static List<User> ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        List<User> list = new ArrayList<User>();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    int i = 1;
                    while (( line = buffreader.readLine()) != null && line.trim().length() > 0 ) {
                        String[] strArray = null;
                        strArray = line.split(",");
                        User user = new User(0,strArray[0],strArray[1]);
                        user.number = i;
                        list.add(user);
                        i++;
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return list;
    }
    //读取文本文件中的内容
    public static List<User> ReadTxtFile(String strFilePath,int number)
    {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        List<User> list = new ArrayList<User>();
        int count = 0;
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        count++;
                        if(count==number){
                            String[] strArray = null;
                            strArray = line.split(",");
                            User user = new User(0,strArray[0],strArray[1]);
                            user.number = number;
                            list.add(user);
                        }
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return list;
    }
    public static void writeTxtFile(List<User> list ,String path,String name) {
        String filePath = path;
        String fileName = name;
        //生成文件夹之后，再生成文件，不然会出错
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath +"/"+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            for(User user:list){
                String content = user.phone+","+user.pwd+"\r\n";
                raf.write(content.getBytes());
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeHongbao(User user,String path,String name) {
        String filePath = path;
        String fileName = name;
        //生成文件夹之后，再生成文件，不然会出错
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath +"/"+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            String content = user.number+","+user.phone+","+user.pwd+","+user.hongbao+","+user.dou+"\r\n";
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writehengxian(int count ,String path,String name) {
        String filePath = path;
        String fileName = name;
        //生成文件夹之后，再生成文件，不然会出错
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath +"/"+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String dateString = formatter.format(new Date());
            String content = "---"+dateString+"开始("+count+")---"+"\r\n";
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeDou(User user,Order order,String path,String name) {
        String filePath = path;
        String fileName = name;
        //生成文件夹之后，再生成文件，不然会出错
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath +"/"+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            String content = order.id+","+user.number+","+user.phone+","+user.pwd+","+user.dou+","+user.send_dou+","+user.last_dou+"\r\n";
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
