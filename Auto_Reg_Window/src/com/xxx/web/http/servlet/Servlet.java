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
            logger.info("进入servlet: "+this.getClass().getName()+"...");
            String path = "";
            String uri = request.getRequestURI();
            int pos = uri.indexOf("/", 1);
            path = uri.substring(pos + 1);
            if (!path.isEmpty()){
            	//若路径中存在".",则肯定是错误的class名称
                if (path.indexOf(".") > 0) {
                    response.sendError(404);
                    return;
                }
            }
            //根据路径生成相应的Action对象
            String className = "com.xxx." + path.replace("/", ".");
            logger.info("通过反射实例化对应的Action，className为:"+className);      
        	Action action = (Action) Class.forName(className).newInstance();
            if (action != null && action instanceof Action){
                logger.info("实例化成功，执行默认方法...");       
                invokeAction(request, response, action);
                logger.info("完成默认方法...");      
                logger.info("完成Action:"+className+"处理...");     
            }else{
                response.sendError(404);
            }
            logger.info("完成servlet:"+this.getClass().getName()+"跳转并返回...");
        }catch (Exception e){
        	e.printStackTrace();
        	response.sendError(404);
        }
    }
    /**
     * 调用相应的Action处理，并跳转
     * @param request
     * @param response
     * @param action
     * @throws IOException 
     */
    private void invokeAction(HttpServletRequest request, HttpServletResponse response, Action action) throws IOException{
        try{
            ActionResult actionResult = action.execute(request, response);
            if (actionResult != null){
                if (actionResult.isDispatch()){//容器内跳转(forword)
                    ServletContext context = servletConfig.getServletContext();
                    RequestDispatcher dispatcher = context.getRequestDispatcher(actionResult.getPage());
                    dispatcher.forward(request, response);
                }else{//容器外跳转(Redirect)   
                    response.sendRedirect(request.getContextPath() + actionResult.getPage());
                }
            }
        }catch (Exception ex){
        	ex.printStackTrace();
            response.reset();
            response.sendError(500);
        }
    }
}
