package com.xxx.lfs.function;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.xxx.web.http.listener.Listener;
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

	protected Logger logger = Logger.getLogger(this.getClass());
	protected ResponseParameter response = new ResponseParameter();

}