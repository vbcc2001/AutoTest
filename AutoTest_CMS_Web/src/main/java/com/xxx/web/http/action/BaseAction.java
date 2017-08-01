package com.xxx.web.http.action;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础action类
 * @author 门士松  20121031
 * @version 1.0
 * @since
 */
public class BaseAction implements Action {

	protected Logger logger = Logger.getLogger(this.getClass());
	protected List<String> actionErrors = new ArrayList<String>();
	protected List<String> actionMessages = new ArrayList<String>();
	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;
    /**
     * 实现Action中的execute方法
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
        this.request = request;
        this.response = response;
        return execute();
    }

    /**
     * 根据function的不同的值，再执行相应的调用
     * 若function为空字串，则调用doDefault方法，否则调用doXXX方法，xxx为function的名称
     * 在调用相应的方法之前会先调用before方法，在调用方法之后，会调用after方法。
     *
     * @return
     */
    private ActionResult execute() throws Exception{
        String function = request.getParameter("function");
        String method = (function==null||function.length() == 0) ? "default" : function;
        method = "do" + method.substring(0, 1).toUpperCase() + method.substring(1);
        //在进入相应的function对应的方法前，先调用before方法
    	logger.info("进入function方法前执行before方法...");
        before(method);
    	logger.info("进入function方法...");
        ActionResult result = (ActionResult) MethodUtils.invokeMethod(this, method, null);
        //在调用相应的方法后，再调用after
    	logger.info("进入function方法后执行after方法...");
        after(method);
        return result;
    }


    /**
     * 在调用相应的function方法前调用,子类可以覆盖,在进入方法前做一些处理
     * @param function 业务功能参数
     */
    public void before(String function){
    }
    /**
     * 在调用相应的function之后调用，子类可以覆盖,在离开方法后再进行一些相应的处理
     * @param function 业务功能参数
     */
    public void after(String function){
    }
    /**
     * 缺省的操作(function=""时调用)
     */
    public ActionResult doDefault() throws Exception{
        return null;
    }
    /**
     * 返回HttpServletResponse对象
     */
    public HttpServletResponse jsonContent(){
        return response;
    }
    /**
     * 返回PrintWriter对象
     */
    public PrintWriter getWriter(){
        try {
			return response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
        	return null;
		}
    }
    /**
     * 返回HttpServletResponse对象
     */
    public HttpServletResponse getResponse(){
        return response;
    }
    /**
     * 返回HttpServletRequest对象
     */
    public HttpServletRequest getRequest(){
        return request;
    }    
    /**
     * 返回HttpSession对象
     */
    public HttpSession getSession(){
        return getRequest().getSession();
    }
    /**
     * 返回ServletContext对象
     */
    public ServletContext getApplication(){
        return getSession().getServletContext();
    }
    /**
     * 添加错误信息
     */
    public void addActionError(String error){
        actionErrors.add(error);
    }
    /**
     * 获得错误信息
     */
    public List<String> getActionErrors(){
        return actionErrors;
    }
    /**
     * 判断是否有错误
     */
    public boolean hasActionErrors(){
        return actionErrors.size() > 0;
    }
    /**
     * 添加消息
     */
    public void addActionMessages(String message){
        actionMessages.add(message);
    }
    /**
     * 获得所有消息
     */
    public List<String> getActionMessages(){
        return actionMessages;
    }
    /**
     * 判断是否有消息
     */
    public boolean hasActionMessages(){
        return actionMessages.size() > 0;
    }
    /**
     * 清除所有的错误和消息
     */
    public void clearErrorsAndMessages(){
        actionMessages.clear();
        actionErrors.clear();
    }
    /**
     * 判断是否是以post方式提交
     */
    public boolean isPostBack(){
        String method = getRequest().getMethod();
        if ("POST".equalsIgnoreCase(method)){
            return true;
        }
        return false;
    }
    /**
     * 返回字串Parameter (进行转换<>这种尖括号等)
     * @param fieldName 表单字段名称
     * @return 字符串，如果为NULL返回NUll
     */
    public String getStrParameter(String fieldName){
    	return this.encodeURL(request.getParameter(fieldName).trim());
    }
	/**
	 *为了防止跨站脚本攻击，转换<>这种尖括号。
	 */
	public String encodeURL(String source){
		if (source != null){
			String html = new String(source);
			html =html.replace("<", "&lt;");
			html =html.replace(">", "&gt;");
			html =html.replace("\"", "&quot;");
			html =html.replace(" ", "&nbsp;");
			html =html.replace("\'", "&acute;");
			html =html.replace("\\", "&#092;");
			html =html.replace("&", "&amp;");
			return null;
		}else{
			return source;
		}   
	}
    /**
     * 返回字串数组Parameter，若不存在，则返回空字符串数组
     * @param fieldName 字段名称
     * @return 字符串数组
     */
    public String[] getStrArrayParameter(String fieldName){
        String[] valueArray = request.getParameterValues(fieldName);
        if(valueArray!=null){
	        for (String value : valueArray){
	        	value = this.encodeURL(value);
	        }
        }
        return valueArray;
    }
    /**
     * 返回整数，若不存在或转换失败，则返回0
     * @param fieldName
     */
    public Integer getIntParameter(String fieldName){
        return Integer.parseInt(getStrParameter(fieldName));
    }
    /**
     * 获得客户的真实IP地址
     */
    public String getIpAddress(){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
