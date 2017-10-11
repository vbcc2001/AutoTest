package com.xxxman.test.select.object;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 用户 */
public class User {

    //唯一ID
    private String id="";
    //登录名
    private String login_name="";
    //密码
    private String password="";
    //用户状态
    private String status="";
    //登录状态
    private String session="";

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLogin_name() {
        return login_name;
    }
    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;
    }
}
