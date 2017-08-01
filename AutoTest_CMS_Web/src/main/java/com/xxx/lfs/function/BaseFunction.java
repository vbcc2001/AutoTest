package com.xxx.lfs.function;

import org.apache.log4j.Logger;

import com.xxx.web.function.Function;
import com.xxx.web.function.ResponseParameter;

/**
 * Service类的基类
 * @author 门士松  20121030
 * @version 1.0
 * @since
 */
public abstract class BaseFunction implements Function {
	
	Logger logger = Logger.getLogger(this.getClass());
	ResponseParameter response = new ResponseParameter();	
}