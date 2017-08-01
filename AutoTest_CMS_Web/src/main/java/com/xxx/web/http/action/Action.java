package com.xxx.web.http.action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http请求的Action接口
 * @author 门士松  20121228
 * @version 1.0
 * @since
 */
public interface  Action {
	public ActionResult execute(HttpServletRequest request,HttpServletResponse response) throws Exception;
}