package com.xxxman.test.select.util;

import android.os.Environment;
import android.util.Log;

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
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath();
            String fileName = name;
            //生成文件夹之后，再生成文件，不然会出错
            File file = null;
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
            String content = text+"\r\n";
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
