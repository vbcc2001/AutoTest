package com.xxx.lfs.action;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.Function;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.helper.PageHelper;
import com.xxx.web.http.action.ActionResult;
import com.xxx.web.http.action.BaseAction;
import com.xxx.web.http.listener.Configure;

/**
 * http服务入口
 * @author 门士松  20121031
 * @version 1.0
 * @since
 */
public class FunctionAction extends BaseAction {
	private Logger logger = Logger.getLogger(this.getClass());
	private ResponseParameter responseParameter = new ResponseParameter();
	public ActionResult doDefault() {
		if (this.isPostBack()) {
			String jsonContent = "";
			try {
				getRequest().setCharacterEncoding("UTF-8");
				//标准http请求
				jsonContent = getRequest().getParameter("jsonContent");
				//非标准http请求（android，ios）
				if(null==jsonContent || "".equals(jsonContent)) { //post的内容体作为一个参数
					try {
						InputStreamReader isr = new InputStreamReader(this.getRequest().getInputStream(),"UTF-8");	
						StringBuffer sb = new StringBuffer();
						while (isr.ready()) {
	                          sb.append((char) isr.read());
	                    }
						jsonContent = sb.toString();
					} catch (IOException e1) {
						e1.printStackTrace();
						ResponseParameter responseParameter = new ResponseParameter();
						responseParameter.setErrorNo("-1");
						responseParameter.setErrorInfo("未获取到参数导致暂时无法访问,请联系客服。");
						sendHttpResponse(responseParameter);
					}
				}
				logger.info("httpRequest请求的json参数为："+jsonContent);
				String request_ip = this.getIpAddress();
				String path = this.getRequest().getContextPath();
				String request_path = this.getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+path;
				Gson gson = new GsonBuilder().serializeNulls().create();
				RequestParameter requestParameter = (RequestParameter) gson.fromJson(jsonContent, new TypeToken<RequestParameter>() {}.getType());    
				requestParameter.setRequest_ip(request_ip);
				requestParameter.setRequest_path(request_path);
				responseParameter = this.execute(requestParameter);
				this.sendHttpResponse(responseParameter);			
			} catch (Exception e) {
				e.printStackTrace();
				responseParameter.setErrorNo("-1");
				responseParameter.setErrorInfo("参数解析错误");
				sendHttpResponse(responseParameter);
			}	
		} else { //get请求
			responseParameter.setErrorNo("-1");
			responseParameter.setErrorInfo("暂不提供get方式的请求");
			sendHttpResponse(responseParameter);
		}
		return null;
	}
	/**
	 * 返回处理
	 * @param response
	 * @param request
	 * @param response2
	 */
	private void sendHttpResponse(ResponseParameter responseParameter) {
		logger.info("httpRequest请求处理后返回的信息为："+responseParameter);
		try {
        	String str= toJson(responseParameter);
            this.getResponse().getWriter().print(str);
            this.getResponse().getWriter().flush();
            this.getResponse().getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将responseParameter 转为json格式
	 * @param responseParameter
	 * @return
	 */
    private String toJson(ResponseParameter responseParameter){
		Gson gson = new GsonBuilder().serializeNulls().create();
		//如果分页信息为空，则生成json的时过滤掉page（分页）对象
    	if(responseParameter.getPage()==null){
    		gson = new GsonBuilder()
    			.serializeNulls()
    			.addSerializationExclusionStrategy(new PageHelperExclusionStrategy()) //设置过滤
    			.create();
    	}
		return gson.toJson(responseParameter);
    }
	/**
	 * 自定义的一个Gson 序列化对象过滤器：过滤掉PageHelper(分页)对象
	 */
	private class PageHelperExclusionStrategy implements ExclusionStrategy {
		private Class<?> clazz = PageHelper.class;
		@Override
		public boolean shouldSkipClass(Class<?> arg0) {
			return false;
		}
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return clazz.equals(f.getDeclaredClass());
		}
	}
	 /**
     * 功能号跳转功能
     */
    public ResponseParameter execute(RequestParameter requestParameter){
    	ResponseParameter responseParameter = new ResponseParameter();
        try{	
            //判断是否已经登录或不需要登陆
            if(isLogin(requestParameter)){
    			logger.info("进入具体功能号："+requestParameter.getFunction());		
    			String path = Configure.getConfig("function-path");
	            String className = path+"." + requestParameter.getFunction();
	            Function functionObj = (Function) (Class.forName(className).newInstance());
	            responseParameter = functionObj.execute(requestParameter);
            }else {
            	responseParameter.setErrorNo("401");
            	responseParameter.setErrorInfo("您尚未登录！");
            }
        }
        catch (Exception e){
        	e.printStackTrace();
        	responseParameter.setErrorNo("500");
        	responseParameter.setErrorInfo("系统内部错误！");
        }
        return responseParameter;
    }
    /**
     * 判断是否已经登录或不需要登陆
     * @throws SQLException 
     */
    public boolean isLogin(RequestParameter requestParameter) throws SQLException  {	
    	
    	String functions =  ""; //不需要登陆的功能号
    	boolean flag = false;
    	//不需要登录判断
    	if(functions!=null && functions.indexOf(requestParameter.getFunction())>=0 ){
    		flag = true;
	    }else{
	    	//目前不用登陆验证
	    	flag = true;
    		//验证登陆
	    	//F000001 f000001 = new F000001();
    		//登陆验证
    		//flag = f000001.isLogin(requestParameter.getSessionID());
    	}
    	return flag;
    }
}
