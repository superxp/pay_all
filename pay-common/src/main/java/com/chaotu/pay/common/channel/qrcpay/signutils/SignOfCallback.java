package com.chaotu.pay.common.channel.qrcpay.signutils;

import com.chaotu.pay.common.channel.qrcpay.util.Common;
import com.chaotu.pay.common.channel.qrcpay.util.LocalUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * 交易异步回调验签工具类
 * @author pk
 *
 */
public class SignOfCallback {

	private static final Logger log = LoggerFactory.getLogger(SignOfCallback.class);

	public static String prikey = "x/MhCZXbxIKnyrz7OTCesYMpsTPWhlHZ8jTPkOcTfXUfglIMHFEfXoE3kx4FOHw7fo4iq97Fz7oQs+yEVVN+b82PPDzSU+vwYZnOsriD2hyHST9bBmAfdAgMBAAECgYB8BomGyhUlggJjFiO5oSXQ135qslT1PZA4hqPCHQU9zDpYWFPW5up7Sb0qTNuf7mK6mtm2OUxZcaqfnAkLNolQwxe3hLVPnjY/PgFFj1H0H+Q5LuIylafzVplMQP4w1LaWwI9bTXcun95l4MhGiX/GgL+DC8UtN4SdTGTR+YFeyQJBAPbHN2FnCDZCreV2ke+t0y0BDxQzhOEHYP76XJDkLUnzOzXpWQFGSWOWkIlvTuwL5suI0pAGAd1hRkwXTTks+KcCQQC3YE+pzS3K7qIboLBcj6V7dtp+yQvZQL0hEnJtdlPPywHuDLdLK+d53OYpQJZK19sj7EuF1bDQvUCzVp8Tr0fbAkBY9pSGbqhNN1iNYmNV7mo0zS7sb2412X0kLol0Sm3xPNhZpRfLF9WZK0xL326qNdgsA9U0ypWdg1XeVIAoXof9AkEAgLaglC5Js0R3Eu9ie0X0wMm3KgESSQcw+bLKEN1ajOMQ0reoPKJvktEj2NbV52jGD9v/UsyglJQSgw8ND7MbuwJAJVodmCM7nmMTZFFZs6mzOTm/2ovPHguOknTypAJWwX0A89MeZSOsdg7ToQIC2J+IQ4YXsLoK+obEmSqzOFoeBQ==";
	public static String publickey = "x+cz6BQOCLSanAdiA2L1hsh21rOsl9ASUikCtPzIQmV28SCp8q8+zkwnrGDKbEz1oZR2fI0z5DnE311H4JSDBxRH16BN5MeBTh8O36OIqvexc+6ELPshFVTfm/Njzw80lPr8GGZzrK4g9och0k/WwZgH3QIDAQAB";

	
	/**
	 * 交易异步回调验签
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//回调sign
		String date = "IxRwYpjTa+x+2u42ctqkGnIZHE5iIBKWfzVufOW8aoN/UZ0b1dD1N+PUdMVxcVaEN7gmKJtMOGvKVcgPTYCW8drbFTEWaZUmw1MNjpnwFgsrKoUEUeO83hfJeB6KHZ/ijyGKiBFY8=";
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		//回调业务数据
		dataMap.put("respCode", "0000");
		dataMap.put("respInfo", "交易成功");
		dataMap.put("amount", "x");
		dataMap.put("channelNum", "x");
		dataMap.put("appOrderId", "x");
		dataMap.put("orderId", "x");
		
		System.out.println(dataMap.toString());
		String signData = Base64.encodeBase64String(getParString(dataMap)
				.getBytes());

		log.debug("signData|" + signData + "|");

		boolean vfy = LocalUtil.verifySignature(
				Base64.decodeBase64(publickey.getBytes()), date,
				signData.getBytes(Common.CHARSET));
		log.debug("验证签名 " + vfy);
	}

	/**
	 * 解决JDK版本导致的验签不过问题
	 * 
	 * @param signMap
	 * @return
	 */
	public static String getParString(Map<String, Object> signMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("amount=" + signMap.get("amount") + ", ");
		sb.append("respCode=" + signMap.get("respCode") + ", ");
		sb.append("channelNum=" + signMap.get("channelNum") + ", ");
		sb.append("appOrderId=" + signMap.get("appOrderId") + ", ");
		sb.append("orderId=" + signMap.get("orderId") + ", ");
		sb.append("respInfo=" + signMap.get("respInfo") + "");
		sb.append("}");
		log.debug("解决JDK版本导致的验签不过"+sb.toString());
		return sb.toString();
	}

}
