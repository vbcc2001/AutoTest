package com.xxx.web.http.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 描述:编码过滤器
 * @author 门士松  20130104
 * @version 1.0
 * @since
 */
public class Encoding implements Filter{
	protected String encoding = "UTF-8";
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain){
        try{
        	request.setCharacterEncoding(encoding);
        	response.setCharacterEncoding(encoding);
        	response.setContentType("text/html; charset=" + encoding);
            chain.doFilter(request, response);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
	@Override
	public void destroy() {
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}	
}
