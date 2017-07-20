package com.xxx.lfs.function;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.xxx.web.function.DataRow;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.lfs.object.Lfs_post;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.http.listener.Configure;

/** 添加  */
public class F100001 extends BaseFunction   {

	@Override
	public ResponseParameter execute(RequestParameter requestParameter) throws Exception {
		String code = requestParameter.getContent().get("code");
		String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
				"n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
				"3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
				"0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
				"4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
				"ioZotQIDAQAB";
		RSAUtils.loadPublicKey(pubkey);
		String enctytCode = RSAUtils.encryptWithRSA(code);

		String sn = SNUtil.getMD5(enctytCode).substring(0, 12);
		System.out.print("------------------"+sn);
		List<DataRow> list = new ArrayList<DataRow>();
		DataRow dr = new DataRow();
		dr.put("sn",sn);
		list.add(dr);
		response.setList(list);
		return response;
	}
	public static void main(String arg[] ) throws Exception{
		String code = "1";
		String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
				"n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
				"3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
				"0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
				"4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
				"ioZotQIDAQAB";
		RSAUtils.loadPublicKey(pubkey);
		String enctytCode = RSAUtils.encryptWithRSA(code);
		String sn = SNUtil.getMD5(enctytCode).substring(0, 12);
		System.out.print("------------------"+sn);

	}
}
