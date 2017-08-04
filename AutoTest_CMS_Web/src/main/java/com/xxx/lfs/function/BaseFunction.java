package com.xxx.lfs.function;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
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

	protected static AmazonDynamoDBClient dynamoDB;
	protected Logger logger = Logger.getLogger(this.getClass());
	protected ResponseParameter response = new ResponseParameter();

	protected static void initAmazonDynamoDB() throws Exception {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
					e);
		}
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.AP_NORTHEAST_1);
		dynamoDB.setRegion(usWest2);
	}

}