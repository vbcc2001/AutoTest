package com.xxx.web.http.action;
/**
 * http请求的Action结果类
 * @author 门士松  20121228
 * @version 1.0
 * @since
 */
public class ActionResult{
    private boolean dispatch = true;
    private String page = "";
    /**
     * @param page    模型页面
     * @param dispatch 是否是容器内分派(true:forward方式 false:redirect方式）
     */
    public ActionResult(String page, boolean dispatch){
        this.page = page;
        this.dispatch = dispatch;
    }

    /**
     * 缺省include值为true
     * @param page
     */
    public ActionResult(String page){
        this.page = page;
        this.dispatch = true;
    }
    /**
     * 返回模型页面路径
     * @return
     */
    public String getPage(){
        return page;
    }
    /**
     * 判断否是容器内分派
     * @return
     */
    public boolean isDispatch(){
        return dispatch;
    }
}