package com.xxxman.test.select.object;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private String function = ""; //功能号码
    private User user = new User();	//用户信息
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
	public Map<String, String> getContent() {
		return content;
	}
	public void setContent(Map<String, String> content) {
		this.content = content;
	}
}
