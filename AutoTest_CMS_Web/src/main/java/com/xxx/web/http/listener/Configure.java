package com.xxx.web.http.listener;

import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * 配置处理类
 * @author 门士松  20121027
 * @version 1.0
 * @since
 */
public class Configure{
	
    private static Document config = null; 
    /**
     * 初始化配置
     * Document ：xml格式配置文件
     */
    public void loadConfig(){
	    InputStream is = Listener.class.getResourceAsStream("/config.xml");
	    SAXReader reader = new SAXReader();
		try {
			config = reader.read(is);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
    }
    /**
     * 根据datasource.xml文件中,得到对应的数据源对象
     * @param name 
     * @return 数据源
     */
    public static Element  getConfig(){
        return config.getRootElement();
    }
    public static String getConfig(String name){
        return config.getRootElement().element(name).getText();
    }
}