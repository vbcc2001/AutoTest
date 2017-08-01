package com.xxx.lfs.object;

public class Lfs_meta {

	//唯一ID
	private String id="";
	//源对象ID
	private String source_id="";
	//键名
	private String key="";
	//键值
	private String value="";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSource_id() {
		return source_id;
	}
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
