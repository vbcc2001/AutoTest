package com.xxx.huajiao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tuzi on 2017/9/30.
 */
public class Data {
    String name = "";
    String total = "";
    String offset = "";
    String more = "";
    List<Feeds> feeds = new ArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public List<Feeds> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feeds> feeds) {
        this.feeds = feeds;
    }

}
