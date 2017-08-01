package com.xxx.lfs.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lfs_post implements Serializable {
	private static final long serialVersionUID = 1L;
	//唯一ID
	private String id="";
	//标题
	private String title = "";
	//内容
	private String content = "";
	//摘录
	private String excerpts = "";
	//分类
	private String type="";
	//作者
	private String author="";
	//创建日期
	private Date create_date=new Date();	
	//标签
	private List<String> tags = new ArrayList<String>();
	//元数据
	private Map<String,String> meta = new HashMap<String,String>();
	//状态
	private String status="";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExcerpts() {
		return excerpts;
	}
	public void setExcerpts(String excerpts) {
		this.excerpts = excerpts;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public Map<String, String> getMeta() {
		return meta;
	}
	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
