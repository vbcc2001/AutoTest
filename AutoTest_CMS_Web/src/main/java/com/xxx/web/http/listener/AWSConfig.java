package com.xxx.web.http.listener;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.xxx.lfs.function.BaseFunction;
import org.apache.log4j.Logger;

/**
 * Created by tuzi on 2017/8/4.
 */
public class AWSConfig {

    protected static AmazonDynamoDBClient dynamoDBClient;
    protected Logger logger = Logger.getLogger(this.getClass());
    public void loadConfig() {
        AWSCredentials credentials = null;
        String path = BaseFunction.class.getResource("/credentials").getPath();
        logger.info("认证信息配置路径为："+path);
        try {
            credentials = new ProfileCredentialsProvider(path,null).getCredentials();
        } catch (Exception e) {
            logger.error("Cannot load the credentials from the credential profiles file. " + path,e.fillInStackTrace());
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. " + path, e);
        }
        dynamoDBClient = new AmazonDynamoDBClient(credentials);
        Region usWest2 = Region.getRegion(Regions.AP_NORTHEAST_1);
        dynamoDBClient.setRegion(usWest2);
    }
    public static AmazonDynamoDBClient getDynamoDBClient(){
        return dynamoDBClient;
    }
    public static DynamoDB getDynamoDB(){
        return new DynamoDB(dynamoDBClient);
    }
    public static DynamoDBMapper getDynamoDBMapper(){
        return new DynamoDBMapper(dynamoDBClient);
    }
}
