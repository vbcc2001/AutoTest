package com.xxxman.test.select.util;


import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by tuzi on 2017/10/25.
 */

public class NanoHttpServer extends NanoHTTPD {

    private static final String TAG = NanoHttpServer.class.getName();

    public NanoHttpServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d(TAG,"进入NanoHttpServer，请求地址为："+session.getUri());
        String r = session.getUri();
        if(r.length()>=4){
            r = r.substring(r.length()-4,r.length());
            if(".jpg".equals(r)){
                Log.d(TAG,"*****请求匹配.jpg*****:"+session.getUri());
                try {
                    //String path = Environment.getExternalStorageDirectory().getCanonicalPath();
                    //FileInputStream fis = new FileInputStream(path+"/XY0.jpg");
                    InputStream fis  =  Base64JPG.toInputStream();
                    return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "image/jpeg", fis,fis.available());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            else if(".flv".equals(r)){
//                Log.d(TAG,"*****请求匹配.flv*****:"+session.getUri());
//                try {
//                    String path = Environment.getExternalStorageDirectory().getCanonicalPath();
//                    FileInputStream fis = new FileInputStream(path+"/XY0.flv");
//                    return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "video/x-flv", fis,fis.available());
//                } catch (Exception e) {
//                    //e.printStackTrace();
//                }
//            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("404 -- Sorry, Can't Found "+ session.getUri() + " !");
        builder.append("</body></html>\n");
        return newFixedLengthResponse(builder.toString());
//        /*我在这里做了一个限制，只接受POST请求。这个是项目需求。*/
//        if (Method.POST.equals(session.getMethod())) {
//            Map<String, String> files = new HashMap<String, String>();
//            /*获取header信息，NanoHttp的header不仅仅是HTTP的header，还包括其他信息。*/
//            Map<String, String> header = session.getHeaders();
//
//            try {
//                /*这句尤为重要就是将将body的数据写入files中，大家可以看看parseBody具体实现，倒现在我也不明白为啥这样写。*/
//                session.parseBody(files);
//                /*看就是这里，POST请教的body数据可以完整读出*/
//                String body = session.getQueryParameterString();
//                CNTrace.d("header : " + header);
//                CNTrace.d("body : " + body);
//                /*这里是从header里面获取客户端的IP地址。NanoHttpd的header包含的东西不止是HTTP heaer的内容*/
//                header.get("http-client-ip"),
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ResponseException e) {
//                e.printStackTrace();
//            }
//            /*这里就是为客户端返回的信息了。我这里返回了一个200和一个HelloWorld*/
//            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", "HelloWorld");
//        }else
//            return newFixedLengthResponse(Status.NOT_USE_POST, "text/html", "use post");
//
    }




}
