package com.xxxman.autotest.shell;

public class Order {

    public int id = 0;
    public int huajiao_id =0;
    public int max_dou =0;
    public int per_dou = 0;

    public Order(int id , int huajiao_id, int per_dou,int max_dou){
        this.id = id;
        this.huajiao_id = huajiao_id;
        this.per_dou = per_dou;
        this.max_dou = max_dou;
    }
    public Order(int id ){
        this.id = id;
    }
    public Order(){
    }
}
