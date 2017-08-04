package com.xxx.lfs.function;

import java.text.SimpleDateFormat;
import java.util.*;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.http.listener.AWSConfig;


/** 添加  */
public class F100002 extends BaseFunction {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {

		DynamoDB dynamo = AWSConfig.getDynamoDB();
		Table table = dynamo.getTable("sun");

		return response;
	}

	private static void FindRepliesInLast15Days(DynamoDBMapper mapper, String forumName, String threadSubject) throws Exception {

		System.out.println("FindRepliesInLast15Days: Replies within last 15 days.");

		String partitionKey = forumName + "#" + threadSubject;

//		long twoWeeksAgoMilli = (new Date()).getTime() - (15L * 24L * 60L * 60L * 1000L);
//		Date twoWeeksAgo = new Date();
//		twoWeeksAgo.setTime(twoWeeksAgoMilli);
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//		String twoWeeksAgoStr = dateFormatter.format(twoWeeksAgo);

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		//eav.put(":编号", new AttributeValue().withS(partitionKey));
		eav.put(":val1", new AttributeValue().withN("1"));
		eav.put(":val2", new AttributeValue().withS("2"));
		//eav.put(":序号", new AttributeValue().withS(twoWeeksAgoStr.toString()));

		DynamoDBQueryExpression<Sun> queryExpression = new DynamoDBQueryExpression<Sun>()
				.withKeyConditionExpression("编号 > :val1 ").withExpressionAttributeValues(eav);

		//List<Sun> latestReplies = mapper.query(Sun.class, queryExpression);

		List<Sun> latestReplies = mapper.query(Sun.class,queryExpression);

		for (Sun sun : latestReplies) {
			//System.out.format(""+sun.getId()+","+ sun.getNumber(), sun.getSun(), sun.getDate());
		}
	}
	private static void GetSun(DynamoDBMapper mapper, int id) throws Exception {
		System.out.println("GetBook: Get book Id='101' ");
		System.out.println("Book table has no sort key. You can do GetItem, but not Query.");
		Sun sun = mapper.load(Sun.class, 123,"18926085629");
		System.out.println("----"+sun.getId()+","+ sun.getNumber()+","+sun.getSun()+","+sun.getDate());
	}
	public static void main(String[] args) throws Exception {
		new AWSConfig().loadConfig();
		//F100001 f = new F100001();
		try {

			DynamoDBMapper mapper = AWSConfig.getDynamoDBMapper();

			// Get a book - Id=101
			GetSun(mapper, 123);
			// Sample forum and thread to test queries.
			String forumName = "Amazon DynamoDB";
			String threadSubject = "DynamoDB Thread 1";
			// Sample queries.
			//FindRepliesInLast15Days(mapper, forumName, threadSubject);
			//FindRepliesPostedWithinTimePeriod(mapper, forumName, threadSubject);

			// Scan a table and find book items priced less than specified
			// value.
			//FindBooksPricedLessThanSpecifiedValue(mapper, "20");

			// Scan a table with multiple threads and find bicycle items with a
			// specified bicycle type
			//int numberOfThreads = 16;
			//FindBicyclesOfSpecificTypeWithMultipleThreads(mapper, numberOfThreads, "Road");

			System.out.println("Example complete!");

		}
		catch (Throwable t) {
			System.err.println("Error running the DynamoDBMapperQueryScanExample: " + t);
			t.printStackTrace();
		}
	}
	@DynamoDBTable(tableName = "sun")
	public static class Sun {
		private int id;
		private int number;
		private String user;
		private String pwd;
		private int date;
		private boolean starte;
		private int sun;

		// Partition key
		@DynamoDBHashKey(attributeName = "编号")
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		// Range key
		@DynamoDBRangeKey(attributeName = "用户名")
		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		@DynamoDBAttribute(attributeName = "序号")
		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}



		@DynamoDBAttribute(attributeName = "密码")
		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		@DynamoDBAttribute(attributeName = "日期")
		public int getDate() {
			return date;
		}

		public void setDate(int date) {
			this.date = date;
		}

		@DynamoDBAttribute(attributeName = "是否成功")
		public boolean setStarte() {
			return starte;
		}

		public void setStarte(boolean starte) {
			this.starte = starte;
		}

		@DynamoDBAttribute(attributeName = "阳光数")
		public int getSun() {
			return sun;
		}

		public void setSun(int sun) {
			this.sun = sun;
		}

	}
}
