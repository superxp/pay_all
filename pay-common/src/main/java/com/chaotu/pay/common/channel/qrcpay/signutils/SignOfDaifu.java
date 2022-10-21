package com.chaotu.pay.common.channel.qrcpay.signutils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * 代付异步回调验签
 * @author pk
 *
 */
public class SignOfDaifu {

	public static String prikey = "x+G4S2TJ1d5vnGfhyJHMUvOuqZqrwvKKMc6m0aHt4SpME7hPCd8HYzUoNVjqPH/PEciUDW8r6fzmWbGsuFLNXPI3V5cS7Flnsl0+FG+G15F66HqiIZmSXTAONERt6NIqTyTPZ7aZynWvAgMBAAECgYBrDwcJm7nMad7JZOg0584fgHZg3eSdC3Zp5R1oynakQHzcV1vlCqXSAhQG4glKmkAdW/7d8gAWqGPVSlEoxHneRPC5c16pMAloSp26xwGgwyhbjQ7HkvqTP65c3rkxtXX+9y2t/PTHiZaA8IT1XO+ya6nwWjaroGuTwbLnP/KXsQJBANwZY1I+MRYSRyOlil1wX/SXDlG5wf9TnOs0qPkesxASQQGHS1f0PphXvhrlZJWfq7OtcszsRO0QeAmLl+XPUbsCQQCfe6xpDin81bgw6JULzxgkg7KbeHQzA88Otl+sAjfqEA3DHfC61g2z4Lh9o9u5ucm8ytvQZjHFKx9fqBrOd6KdAkEAu5ff8Y8UY0182MMVZYKMJnpBOCLtWGO5kTc29e3WjNIiGd1ouDRyzYGrhF0UF/sZa3FgMY0HSk8JIImcE8FaVQJBAJ5LCl1biaFCH4er0gPekX/sl6JodC0OYr2flmYL7aOMO5ymYwGO5BrduUA7GyPEm8+fKX43cL7ahlFTcQftBqECQQDanR10tXvKv0DhbW+lnqRl++9Y5MlXbcDfrNjYt6xlN1kXnL4WlZmIk7YNaLGQoQaeVYG6JrG0/oBNEaOBMKvr";
	public static String publickey = "x/huEtkydXeb5xn4ciRzFLzrqmaq8LyijHOptGh7eEqTBO4TwnfB2M1KDVY6jx/zxHIlA1vK+n85lmxrLhSzVzyN1eXEuxZZ7JdPhRvhteReuh6oiGZkl0wDjREbejSKk8kz2e2mcp1rwIDAQAB";

	/**
	 * 代付异步回调验签
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//代付异步回调返回信息
		String date = "mercNum=x&respCode=A000&orderNum=x&sign=x";
		String data1 = date.substring(0, date.indexOf("&sign"));
		String sign1 = date.substring(date.indexOf("&sign") + 6, date.length());
		
		System.out.println(data1);
		System.out.println(sign1);
		String sign = DigestUtils.md5Hex(
				(data1 + "&key=" + publickey).getBytes()).toUpperCase();

		System.out.println("验证签名 " + sign);
		if (sign.equals(sign1)) {
			System.out.println("验签成功");
		} else {
			System.out.println("验签失败");
		}
	}
}
