package com.xxx.lfs.action;

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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * http服务入口
 * @author 门士松  20121031
 * @version 1.0
 * @since
 */
public class CsvAction extends BaseAction {
	private Logger logger = Logger.getLogger(this.getClass());
	private ResponseParameter responseParameter = new ResponseParameter();
	public ActionResult doDefault() {
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
		return null;
	}
	/**
	 * 返回处理
	 */
	private void sendHttpResponse(ResponseParameter responseParameter) {
		logger.info("httpRequest请求处理后返回的信息为："+responseParameter);
		try {
			if("".equals(responseParameter.getErrorNo())){
				String csv = responseParameter.getDataRow().getString("csv");
				String file_name = responseParameter.getDataRow().getString("file_name");
				file_name = URLEncoder.encode(file_name,"utf-8");
				response.setContentType("application/octet-stream; charset=UTF-8");
				response.setCharacterEncoding("GBK");
				response.setHeader("Content-disposition", "attachment; filename="+file_name+".csv");
				response.getWriter().print(csv);
				response.getWriter().flush();
				response.getWriter().close();
			}else{
				response.getWriter().print(responseParameter.getErrorNo()+":"+responseParameter.getErrorInfo());
				response.getWriter().flush();
				response.getWriter().close();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
