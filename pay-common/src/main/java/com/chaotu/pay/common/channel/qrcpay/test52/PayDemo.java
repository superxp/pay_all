package com.chaotu.pay.common.channel.qrcpay.test52;

import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.channel.qrcpay.util.*;

import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.RequestUtil;
import org.apache.commons.codec.binary.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

/**
 * 
 * 
 * 
 * @author pk
 *
 */
public class PayDemo {
	
	public static void main(String[] args) throws Exception {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		//*************************业务参数**********************************************

		dataMap.put("amount", "200000");// 交易金额，单位（分）
		dataMap.put("agentnum", "108");// 机构编号
		dataMap.put("channelNum", "100036");// 渠道编号
		dataMap.put("payCode", "100201");// 业务类型
		dataMap.put("appOrderId", "201011154159295199992");
		dataMap.put("callBackUrl", "http://www.baidu.com");//异步通知地址（可自行上送或发对接群由客服配置）

		dataMap.put("payType", "02");// 客户端类型 02:手机浏览器
		dataMap.put("bankCode","ICBC");
		dataMap.put("accNoType","DEBIT");
		
		dataMap.put("payType", "02");// 客户端类型 02:手机浏览器
		
		//***********************************************************************
		
		System.out.println("业务数据拼接完成|" + dataMap + "|");
		
		String jsonMap = JSONObject.toJSONString(dataMap);

		System.out.println("业务数据转json|" + jsonMap + "|");

		jsonMap = Base64.encodeBase64String(jsonMap.getBytes());

		System.out.println("业务数据json转base64|" + jsonMap + "|");

		//通过私钥进行签名，得到签名
		byte[] mySign = LocalUtil.sign(
				Base64.decodeBase64(Common.PRIVATEKEY.getBytes()), jsonMap);
		
		String reqSign = new String(mySign);

		System.out.println("数据签名|" + reqSign + "|");

		//通过公钥进行数据加密
		byte[] encodedData = RSAUtils2.encryptByPublicKey(jsonMap.getBytes(),
				Common.PUBLICKKEY);

		String reqData = Base64.encodeBase64String(encodedData);

		System.out.println("数据加密|" + reqData + "|");

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("sign", reqSign);
		request.put("data", reqData);
		request.put("account",Common.ACCOUNT);
		System.out.println("上送业务数据|" + request + "|");

		JSONObject realParam = new JSONObject();
		realParam.put("serviceCode", "A0480");
		realParam.put("request", request);

		System.out.println("上送全部数据|" + realParam.toString() + "|");

		String reqParam = String.format("reqParam=%s", URLEncoder.encode(realParam.toJSONString()));


		PddMerchantSender<Map<String,Object>> paySender = new PddMerchantSender<>(Common.URL,reqParam,null);

		System.out.println("响应数据|" + paySender + "|");


		Map<String, Object> rspMap = paySender.send();

		System.out.println("响应数据转Map|" + rspMap + "|");

		JSONObject response = (JSONObject)rspMap.get("response");

		String resp = response.getString("resp");
		if (!response.getString("respCode").equals("0000")) {
			//System.out.println("支付链接获取失败：" + rspCode);
		} else {

			System.out.println("html获取成功，开始验签。");
			parseMap(resp);
			String rspSign = response.get("sign").toString();

			System.out.println("signMap|" + rspMap + "|");

			String signData = Base64.encodeBase64String(getParString(response)
					.getBytes());

			System.out.println("signData|" + signData + "|");

			boolean vfy = LocalUtil.verifySignature(
					Base64.decodeBase64(Common.PUBLICKKEY.getBytes()), rspSign,
					signData.getBytes(Common.CHARSET));
			System.out.println("验证签名 " + vfy);

			System.out.println("获取的html为： " + rspMap.get("resp"));


		}
	}
	
	

	/**
	 * 同步返回验签原串顺序
	 * 该方法用于兼容JDK不同版本
	 * @param signMap
	 * @return
	 */
	public static String getParString(Map<String, Object> signMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("data=" + signMap.get("data") + ", ");
		sb.append("orderId=" + signMap.get("orderId") + ", ");
		sb.append("respCode=" + signMap.get("respCode") + ", ");
		sb.append("respInfo=" + signMap.get("respInfo"));
		sb.append("}");
		return sb.toString();
	}
	public static  SortedMap<String,Object> parseMap(String content){
		//6.Jsoup解析html
		Document document = Jsoup.parse(content);
		//像js一样，通过标签获取title
		System.out.println(document.getElementsByTag("title").first());
		//像js一样，通过id 获取文章列表元素对象
		Element postList = document.getElementById("tdsubmit");
		//像js一样，通过class 获取列表下的所有博客
		Elements inputs = postList.getAllElements();
		//循环处理每篇博客
		SortedMap<String,Object> map = new TreeMap<>();
		for (int i = 1; i < inputs.size(); i++) {
			Element input = inputs.get(i);
			String name = input.attr("name");
			String value = input.attr("value");
			map.put(name,value);
		}

		return map;
	}
}
