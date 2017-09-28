package com.xxx.web.jdbc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mchange.v2.c3p0.DataSources;

/**
 * 数据源配置处理类
 * @author 门士松  20121027
 * @version 1.0
 * @since
 */
public class DBConfigure{
	
    private static Map<String,DataSource> dataSourceMap = new HashMap<String,DataSource>(); 
    private static String _default = "";
    /**
     * 初始化配置
     */
    public void loadConfig(){
	    InputStream is = DBConfigure.class.getResourceAsStream("/datasource.xml");
	    SAXReader reader = new SAXReader();
	    Document document = null;
		try {
			document = reader.read(is);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	    if (document != null){
	        //得到根元素
	        Element rootElement = document.getRootElement();   
	        @SuppressWarnings("unchecked")
			List<Element> funcElementList = rootElement.elements("datasource");  
	        _default = rootElement.attributeValue("default", "");
	        //数据源解析
	        for (Element dsElement : funcElementList){
	            String id = dsElement.attributeValue("id", "");
	            Map<String,String> propMap = new HashMap<String,String>();
		        @SuppressWarnings("unchecked")
	            List<Element> propElementList = dsElement.elements("property");
	            //对数据源参数解析
	            for (Element propElement : propElementList){
	                String name = propElement.attributeValue("name");
	                String value = propElement.getTextTrim();
	                propMap.put(name, value);
	            }
	            DataSource dataSource = buildDataSource(propMap);
	            if (dataSource != null){
	                dataSourceMap.put(id, dataSource);
	            }
	        }           
	    }
    }

	/**
	 * 创建C3P0数据源
	 * @param propMap
	 * @return
	 */
	private DataSource buildDataSource(Map<String,String> propMap)
	{
	    String driverName = propMap.get("driver-name");
	    String url = propMap.get("url");
	    String user = propMap.get("user");
	    String password = propMap.get("password");
	    
        propMap.remove("driver-name");
        propMap.remove("url");
        propMap.remove("user");
        propMap.remove("passowrd");
        
	    try {
			Class.forName(driverName);
	        DataSource unpooled = DataSources.unpooledDataSource(url, user, password);
	        DataSource pooled = DataSources.pooledDataSource(unpooled, propMap);
	        return pooled;			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return null;
	}  
    /**
     * 根据datasource.xml文件中,得到对应的数据源对象
     * @param name 
     * @return 数据源
     */
    public static DataSource getDataSource(String name){
        return dataSourceMap.get(name);
    }
    /**
     * 得到默认的数据源对象 
     * @return 数据源
     */
    public static DataSource getDataSource(){
        return dataSourceMap.get(_default);
    }    
    /**
     * 描述：销毁数据源
     */
    public void destroyDataSource(){
        try{
            for (String key : dataSourceMap.keySet()){
                DataSources.destroy(dataSourceMap.get(key));
            }
        }
        catch (Exception e){
			e.printStackTrace();
        }
    }    
}