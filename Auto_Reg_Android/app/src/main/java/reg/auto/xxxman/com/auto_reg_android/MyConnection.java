package reg.auto.xxxman.com.auto_reg_android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Founder on 2016/4/4.
 * Author:FounderWatts
 */
public class MyConnection {

    /*
    * urlStr:网址
    * parms：提交数据
    * return:网页源码
    * */
    public String getContextByHttp(String urlStr,Map<String,String> parms){
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            writer.write(getStringFromOutput(parms));

            writer.flush();
            writer.close();
            outputStream.close();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String temp;
                while((temp = reader.readLine()) != null){
                    sb.append(temp);
                }
                reader.close();
            }else{
                return "connection error:" + connection.getResponseCode();
            }

            connection.disconnect();
        }catch (Exception e){
            return e.toString();
        }
        return sb.toString();
    }

    private String getStringFromOutput(Map<String,String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for(Map.Entry<String,String> entirty:map.entrySet()){
            if(isFirst)
                isFirst = false;
            else
                sb.append("&");

            sb.append(URLEncoder.encode(entirty.getKey(),"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entirty.getValue(),"UTF-8"));
        }
        return sb.toString();
    }
}