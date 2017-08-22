package com.xxxman.autotest.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drizzt on 2017-08-22.
 */

public class HttpResult<E> {
    private Error error= new Error();
    private List<E> list =new ArrayList<E>();
    public Error getError() {
        return error;
    }
    public void setError(Error error) {
        this.error = error;
    }
    public List<E> getList() {
        return list;
    }
    public void setList(List<E> list) {
        this.list = list;
    }
}
class Error {
    private String num="";
    private String msg="";
    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

