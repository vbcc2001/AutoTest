package com.xxx.web.jdbc;

import java.util.HashMap;

/**
 * 数据行，继承自HashMap方便对数据存取，返回的对象有可能为空
 * @author 门士松  20121030
 * @version 1.0
 * @since
 */
public class DataRow extends HashMap<String,Object> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 添加内容
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value) {
            put(name, value);
    }
	/**
	 * 获得字符串，byte[]转为String，其他类型为toString()
	 * @param name
	 * @return
	 */
    public String getString(String name) {
    	Object obj = this.get(name);
    	if(obj instanceof byte[]){
    		return new String( (byte[])obj);
    	}else{
    		if(obj==null){
    			return null;
    		}else{
            	return obj.toString();
    		}
    	}
    }
    /**
     * 获得Int对象,对象先转为toString(),在转为Integer.parseInt()
     * @param name
     * @return
     */
    public Integer getInt(String name) {
    	Object obj = this.get(name);
    	if(obj instanceof Integer){
    		return (Integer)obj;
    	}else{
    		if(obj==null){
    			return null;
    		}else{
    			return Integer.parseInt(obj.toString());
    		}
    	}
    }
    /**
     * 获得Float对象,对象先转为toString(),在转为Float.parseFloat()
     * @param name
     * @return
     */    
    public Float getFloat(String name) {
    	Object obj = this.get(name);
    	if(obj instanceof Float){
    		return (Float)obj;
    	}else{
    		if(obj==null){
    			return null;
    		}else{
    		  return Float.parseFloat(obj.toString());
    		}
    	}
    }
    /**
     * 将DataRow中所有的值转换为String类型
     */
    public void AlltoString() {
    	for(String key: this.keySet()){
    		this.put(key, this.getString(key));
    	}
    }
}
