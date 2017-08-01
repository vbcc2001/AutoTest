package com.xxx.web.function;


import java.util.HashMap;
import java.util.Map;

import com.xxx.lfs.object.User;

/**
 * 请求参数的服务类
 * @author 门士松  20151031
 * @version 2.0
 * @since
 */
public class RequestParameter {
	private String function = ""; //功能号码
    private User user = new User();	//用户信息
    private String request_path = "";	//请求路径
    private String request_ip = "";	//请求IP
	private Map<String,String> content = new HashMap<String,String>(); //请求内容
	
    public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getRequest_path() {
		return request_path;
	}
	public void setRequest_path(String request_path) {
		this.request_path = request_path;
	}
	public String getRequest_ip() {
		return request_ip;
	}
	public void setRequest_ip(String request_ip) {
		this.request_ip = request_ip;
	}
	public Map<String, String> getContent() {
		return content;
	}
	public void setContent(Map<String, String> content) {
		this.content = content;
	}
}
