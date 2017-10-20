package com.xxx.web.http.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.huajiao.Feeds;
import com.xxx.huajiao.Result;
import com.xxx.web.jdbc.DBConfigure;
import com.xxx.web.jdbc.JdbcTemplate;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet基础类：用来地址转发
 * @author 门士松  20151211
 * @version 2.0
 */
@SuppressWarnings("serial")
public class Servlet extends HttpServlet{
	
	private Logger logger = Logger.getLogger(this.getClass());
    private ServletConfig servletConfig = null;

    @Override
    public void init(ServletConfig servletConfig){
        this.servletConfig = servletConfig;
    }
    /**
     * Service主方法
     * @param request
     * @param response
     * @throws IOException
     */ 
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            System.out.println(request.getRequestURL());//http://localhost:8080/CarsiLogCenter_new/idpstat.jsp
//            System.out.println(request.getRequestURI());//CarsiLogCenter_new/idpstat.jsp
//            System.out.println(request.getContextPath());//CarsiLogCenter_new
//            System.out.println(request.getServletPath());//idpstat.jsp
            System.out.println(request.getQueryString());//action=idp.sptopn
            System.out.println("进入处理流程-----------------------------");

            //http://passport.huajiao.com/task/watch 处理
            if("/task/watch".equals(request.getRequestURI())
                    || "/user/encryptToken".equals(request.getRequestURI())
                    || "/user/getUserInfo".equals(request.getRequestURI())
                    || "/api/getUserMenu".equals(request.getRequestURI())
                    || "/user/me".equals(request.getRequestURI())

                    ){
                String method = request.getMethod();
                if ("POST".equalsIgnoreCase(method)){
                    System.out.println("request is Post");
                    Map<String, String[]> params = request.getParameterMap();
                    String queryString = "";
                    for (String key : params.keySet()) {
                        String[] values = params.get(key);
                        for (int i = 0; i < values.length; i++) {
                            String value = values[i];
                            queryString += key + "=" + value + "&";
                        }
                    }
                    // 去掉最后一个空格
                    queryString = queryString.substring(0, queryString.length() - 1);
                    System.out.println("POST 请求 " + request.getRequestURL() + " " + queryString);
                    String s1 = request.getRequestURL().toString();
                    String s6 = request.getQueryString();
                    String s=HttpRequest.sendPost(s1+"?"+s6, queryString);
                    System.out.println("post 返回--------------"+s);
                    System.out.println("**-------------------------------------------------------------------");
                    response.getWriter().print(s);
                    response.getWriter().flush();
                    response.getWriter().close();
                }else{
                    System.out.println("request is get");
                    String s1 = request.getRequestURL().toString();
                    String s6 = request.getQueryString();
                    String s=HttpRequest.sendGet(s1, s6);
                    System.out.println("get 返回--------------"+s);
                    response.getWriter().print(s);
                    response.getWriter().flush();
                    response.getWriter().close();

                }
            }else if("/feed/getFeeds".equals(request.getRequestURI())){
                String s1 = request.getRequestURL().toString();
                String s6 = request.getQueryString();
                String s=HttpRequest.sendGet(s1, s6);
                System.out.println("httpRequest请求处理后返回的信息为：");
                System.out.println("*-------------------------------------------------------------------");
                //System.out.println(s);
                Gson gson = new GsonBuilder().serializeNulls().create();
                Result result = gson.fromJson(s, new TypeToken<Result>() {}.getType());
                System.out.println("错误信息："+result.getErrno()+":"+result.getErrmsg());

                List<Feeds> feedsList = result.getData().getFeeds();
                System.out.println("feed数量："+feedsList.size());
                for(Feeds feeds :feedsList){
                    if("Y".equals(feeds.getFeed().getShare_redpacket())){
                        System.out.println("有红包，花椒ID为："+feeds.getAuthor().getUid());
                        insert(feeds.getAuthor().getUid());
                    }
                }
                System.out.println("**-------------------------------------------------------------------");
                response.getWriter().print(s);
                response.getWriter().flush();
                response.getWriter().close();
            }else if("/Tag/hot".equals(request.getRequestURI())){
                String s1 = request.getRequestURL().toString();
                String s6 = request.getQueryString();
                String s=HttpRequest.sendGet(s1, s6);
                response.getWriter().print(s);
                response.getWriter().flush();
                response.getWriter().close();
            }else{
                String r = request.getRequestURI();
                r = r.substring(r.length()-4,r.length());
                if(".flv".equals(r)){
                    System.out.println("**请求匹配.flv");
                    request.getRequestDispatcher("/XY0.flv").forward(request, response);
                }else{
                    response.sendError(404);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            response.sendError(404);
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }
    private int insert(String uid) throws Exception {

            Object arg[] = new Object[1];
            arg[0]=uid;
            String sql="INSERT INTO t_hongbao_info(uid,update_time) VALUES (?,now())";
            return new JdbcTemplate().update(sql,arg);
    }
    public static void main(String[] args) throws Exception {
        new DBConfigure().loadConfig();
        Servlet s = new Servlet();
        System.out.print(s.insert("123")+"---");
    }

}
