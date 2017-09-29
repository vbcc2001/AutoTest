package com.xxx.web.http.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xxx.web.http.action.Action;
import com.xxx.web.http.action.ActionResult;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.Enumeration;

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


        if("/feed/getFeeds".equals(request.getRequestURI())){
            String s1 = request.getRequestURL().toString();
            String s6 = request.getQueryString();
            String s=HttpRequest.sendGet(s1, s6);
            System.out.println("httpRequest请求处理后返回的信息为：");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
            System.out.println(s);
            System.out.println("-------------------------------------------------------------------");
            System.out.println("-------------------------------------------------------------------");
        }
        /*

        System.out.println("1、===================================================================");
        String s1 = request.getRequestURL().toString();//http://localhost:8080/CarsiLogCenter_new/idpstat.jsp
        String s3 = request.getRequestURI();//CarsiLogCenter_new/idpstat.jsp
        String s4 = request.getContextPath();//CarsiLogCenter_new
        String s5 = request.getServletPath();//idpstat.jsp
        String s6 = request.getQueryString();//action=idp.sptopn
        System.out.println(s1);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println(s5);
        System.out.println(s6);

        Enumeration names = request.getHeaderNames();
        System.out.println("2、===================================================================");
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            System.out.println(name + ":" + request.getHeader(name));
        }
        System.out.println("now service sessionid :"+request.getSession().getId());
        System.out.println("===================================================================");
        //发送 GET 请求
        String s=HttpRequest.sendGet(s1, s6);
        System.out.println("httpRequest请求处理后返回的信息为：");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------");
        System.out.println(s);
        System.out.println("-------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------");
        //发送 POST 请求
        //String sr=HttpRequest.sendPost("http:/live.huajiao.com/feed/getFeeds", s6);
        //System.out.println(sr);
        //System.out.println("httpRequest请求处理后返回的信息为："+s);
        try {
            response.getWriter().print(s);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
