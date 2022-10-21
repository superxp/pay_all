package com.chaotu.pay.common.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.channel.qrcpay.util.Common;
import com.chaotu.pay.common.channel.qrcpay.util.LocalUtil;
import com.chaotu.pay.common.channel.qrcpay.util.RSAUtils2;
import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;
import com.chaotu.pay.common.utils.TestEncrypt;
import com.chaotu.pay.constant.CommonConstant;
import com.chaotu.pay.po.TChannel;
import com.chaotu.pay.po.TChannelAccount;
import com.chaotu.pay.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class WangGuanChannel extends AbstractChannel {

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();
    private static final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc7luX7cmA256qukMnFFRWgmHifHjTaRDMa5jvAJ7qdU9AmdISrECDlFHIpVkYLLAB7j8qgqrh9q+nuE/1WIo83yD9vpLDsgiE+zUZ9xHmeLjvoDXmKkHhPSBsNH9RYrWnrn6Ru1oglfYcTAnwHYiISEzp3Yrx/eEbTbar9K1ZiQIDAQAB";
    private static final String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANzuW5ftyYDbnqq6QycUVFaCYeJ8eNNpEMxrmO8Anup1T0CZ0hKsQIOUUcilWRgssAHuPyqCquH2r6e4T/VYijzfIP2+ksOyCIT7NRn3EeZ4uO+gNeYqQeE9IGw0f1FitaeufpG7WiCV9hxMCfAdiIhITOndivH94RtNtqv0rVmJAgMBAAECgYAvQmGfb0MyaEhZlvx0aJnd44gSzbN+7bOlNsMBJS3ZU3y/gef5DJXET77q38veKvj/gLpMWqU5Lu7GPtwDzIvNMeLbYqS5TDQS44gxlRrjn81AXVBzX3xjjh6XFRR1r2QctJs7s/MiW5DEKf5j4Q2joqEU2URKP7mtvcRvsktPAQJBAO3xp3ZTxQ7l039EYgPMFge7M401z4l1wVSYh7ITKMa3W0T+7QJHwn7yydm33Oea7Tb7eZgOU9UcMUc0iEmKTWkCQQDtsjVLOHoCag4qYq7VJt3sb/QpVjsdV10ODeyWOcfwTsp48s87JbfwbOS4lBvgYm01pO/kBpHZ4h+FlTtW74chAkEAhjnuB/gVj+PiPUbsK8wzGUVnPV9/pcGBwCETW0cnl4HTwMY2GTU16Ls5VtI7kYN6Eawm2borXGq8+bgOsb2NEQJBALwKTUrypOMgD5DMfM83bj1L2/aPtzhhEsa5kT7O+zNKwbapL/P0xO042ECFOwBqHUdg8j6MS/n4f0NoaYc++sECQHFP64TF2oAbhYlI7AxVqAIm08uoLVpFjxwvEIkHdT7c99B8HklLvswbg2X1bcoH9QDhsViOYiIJ77k9OgmeHfY=";


    private final BigDecimal bigDecimal100 = new BigDecimal(100);

    private static final String successStr = "{\"success\":\"1\"}";

    public WangGuanChannel(TChannel channel, TChannelAccount account) {
        super(channel, account);
    }

    @Override
    public String createSign(Map<String,Object> order) {
        return null;
    }

    @Override
    public String createNotifySign(Map<String, Object> signParam, HttpServletRequest request) {

        return "";
    }

    @Override
    public boolean checkNotify(Map<String, Object> signParam, HttpServletRequest request) {
        JSONObject requestJsonObject = null;
        try {
            requestJsonObject = RequestUtil.getRequestJsonObject(request);
        }catch (Exception e){

        }
        log.info("通道:"+getChannel().getChannelName()+"接收回调开始验证["+requestJsonObject.toJSONString()+"]");
        return true;
    }

    @Override
    public String getSuccessNotifyStr() {
        return successStr;
    }

    @Override
    SortedMap<String, Object> createSignMap(OrderVo order) {


        return null;
    }

    @Override
    public Map<String, Object> requestUpper(OrderVo order, String sign) {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        //*************************业务参数**********************************************

        dataMap.put("amount", order.getAmount().multiply(bigDecimal100).intValue());// 交易金额，单位（分）
        dataMap.put("agentnum", "122");// 机构编号
        dataMap.put("channelNum", "100036");// 渠道编号
        dataMap.put("payCode", "100201");// 业务类型
        dataMap.put("appOrderId", order.getOrderNo());
        dataMap.put("callBackUrl", getChannel().getNotifyUrl() + getChannel().getId() + "/" + order.getOrderNo());//异步通知地址（可自行上送或发对接群由客服配置）

        dataMap.put("payType", "02");// 客户端类型 02:手机浏览器
        //dataMap.put("bankCode","ICBC");
        dataMap.put("accNoType","DEBIT");

        dataMap.put("payType", "02");// 客户端类型 02:手机浏览器

        //***********************************************************************

        System.out.println("业务数据拼接完成|" + dataMap + "|");

        String jsonMap = JSONObject.toJSONString(dataMap);

        System.out.println("业务数据转json|" + jsonMap + "|");

        jsonMap = org.apache.commons.codec.binary.Base64.encodeBase64String(jsonMap.getBytes());

        System.out.println("业务数据json转base64|" + jsonMap + "|");

        //通过私钥进行签名，得到签名
        byte[] mySign = com.chaotu.pay.common.channel.qrcpay.util.LocalUtil.sign(
                org.apache.commons.codec.binary.Base64.decodeBase64(Common.PRIVATEKEY.getBytes()), jsonMap);

        String reqSign = new String(mySign);

        System.out.println("数据签名|" + reqSign + "|");

        //通过公钥进行数据加密
        byte[] encodedData = new byte[0];
        try {
            encodedData = RSAUtils2.encryptByPublicKey(jsonMap.getBytes(),
                    Common.PUBLICKKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String reqData = org.apache.commons.codec.binary.Base64.encodeBase64String(encodedData);

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
        String rspCode = response.getString("respCode");
        if(rspCode.equals("0000")){
            SortedMap<Object,Object> map = parseMap(resp);

            String url= "http://47.243.18.87:8080/order/redirect/form?" +RequestUtil.createPostParamStr(map);

            SortedMap<String,Object> result = new TreeMap<>();
            result.put("userId",order.getUserId());
            result.put("amount",order.getAmount());
            result.put("qrCode",url);
            result.put("success","1");
            result.put("underOrderNo",order.getUnderOrderNo());
            result.put("orderNo",order.getOrderNo());
            result.put("upperOrderNo",response.get("orderId"));
            String resultSign = DigestUtil.createSignBySortMap(result,order.getUserKey()).toUpperCase();
            result.put("sign",resultSign);
            return result;
        }
        log.info("下单失败！失败信息:["+JSONObject.toJSONString(rspMap)+"]");
        return null;
    }
    private boolean checkSign(SortedMap<Object,Object> params,String sign){
        return StringUtils.equals(DigestUtil.createSign(params,getAccount().getSignKey()),sign);
    }
    private  String createSign(SortedMap<String, Object> parameters, String key) {

        String str = "version=1.0&merchantNo="+getAccount().getAccount()+"&channel=ORDER_PAY&content="+parameters.get("content")+"&appsecret="+getAccount().getSignKey();
        String sign = DigestUtils.md5Hex(str);
        return sign; // D3A5D13E7838E1D453F4F2EA526C4766
        // D3A5D13E7838E1D453F4F2EA526C4766
    }
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

    public static  SortedMap<Object,Object> parseMap(String content){
        //6.Jsoup解析html
        Document document = Jsoup.parse(content);
        //像js一样，通过标签获取title
        //像js一样，通过id 获取文章列表元素对象
        Element postList = document.getElementById("tdsubmit");
        //像js一样，通过class 获取列表下的所有博客
        Elements inputs = postList.getAllElements();
        //循环处理每篇博客
        SortedMap<Object,Object> map = new TreeMap<>();
        map.put("url",postList.attr("action"));
        for (int i = 0; i < inputs.size(); i++) {
            Element input = inputs.get(i);
            String name = input.attr("name");
            String value = input.attr("value");
            map.put(name,value);
        }

        return map;
    }
}
