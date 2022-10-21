package com.chaotu.pay.common.channel;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @program: openPlatform
 * @description:   h5支付
 * @author: Mr.YS
 * @CreatDate: 2019/6/3/003 15:25
 */
public class h5pay {
    static String appId = "8a81c1bd76678a0501768995fba6001a";
    static String appKey = "c9fa827360f94511aa8322389eb50c19";
    static String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    static String nonce = UUID.randomUUID().toString().replace("-", "");

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        pay();
    }
    /**
     * 获取h5支付url
     */
    public static void pay() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        JSONObject json = new JSONObject();
        //json.put("msgId", "001");   // 消息Id,原样返回
        json.put("requestTimestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));    // 报文请求时间
        json.put("merOrderId", "117G34567891211"); // 商户订单号
        json.put("srcReserve", "请求系统预留字段"); // 请求系统预留字段
        json.put("mid", "898440148162608"); // 商户号
        json.put("tid", "58311313");    // 终端号
        json.put("instMid", "H5DEFAULT"); // 业务类型

        /* 分账部分 */
//        json.put("divisionFlag", true); // 分账标记
//        json.put("platformAmount", 10); // 平台商户分账金额

        /* subOrders,子订单信息 */
        /*ArrayList<JSON> subOrders = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mid", "子商户号");
        jsonObject.put("totalAmount", 10);
        subOrders.add(jsonObject);*/
        // json.put("subOrders", subOrders);

//        json.put("attachedData", "商户附加数据"); //商户附加数据
        json.put("orderDesc", "账单描述");  // 账单描述
//        json.put("goodsTag", "商品标记");   // 商品标记
//        json.put("originalAmount", 10);     // 订单原始金额
        json.put("totalAmount", 5000);      // 支付总金额
        json.put("expireTime", DateFormatUtils.format(new Date().getTime()+60*60*1000,"yyyy-MM-dd HH:mm:ss"));  // 订单过期时间,这里设置为一小时后
//        json.put("secureTransaction", false);   // 担保交易标识,用于担保交易
        json.put("notifyUrl", "http://172.27.49.240:8080/h5pay/notifyUrl.do");  // 支付结果通知地址
        json.put("returnUrl", "http://172.27.49.240:8080/h5pay/returnUrl.do");  // 网页跳转地址
//        json.put("systemId", "系统id");   // 系统id

        /* 实名认证 */
        /*json.put("name", "实名认证姓名"); // 实名认证姓名
        json.put("mobile", "1345678910");   // 实名认证手机号
        json.put("certType", "IDENTITY_CARD");  // 实名认证
        json.put("certNo", "12434134213124");   // 实名认证证件号
        json.put("fixBuyer", "T");  // 是否需要实名认证*/

//        json.put("limitCreditCard", "false");   // 是否需要限制信用卡支付
//        json.put("secureTransaction", "false"); // 担保交易标识

        // 准备商品信息
        /*List<Goods> goodsList = new ArrayList<Goods>();
        goodsList.add(new Goods("0001", "充值0.01元", 1L, 100L, "Auto", "充值0.01元"));
        goodsList.add(new Goods("0002", "Goods Name", 2L, 200L, "Auto", "goods body"));

        json.put("goodsList", goodsList);*/
        System.out.println("请求报文:\n"+json);
        String param = getOpenBodySigForNetpay(appId, appKey, timestamp, nonce, json.toString());
        String url = "https://api-mop.chinaums.com/v1/netpay/trade/h5-pay"; // 支付宝
//        String url = "http://58.247.0.18:29015/v1/netpay/uac/order"; // 云闪付
        System.out.println("URL:\n" + url + "?" + param);
    }

    /**
     * @param appId   10037e6f66f2d0f901672aa27d690006
     * @param appKey  47ace12ae3b348fe93ab46cee97c6fde
     * @param content json字符串 String body = "{\"merchantCode\":\"123456789900081\",\"terminalCode\":\"00810001\",\"merchantOrderId\":\"20123333644493200\",\"transactionAmount\":\"1\",\"merchantRemark\":\"测试\",\"payMode\":\"CODE_SCAN\",\"payCode\":\"285668667587422761\",\"transactionCurrencyCode\":\"156\"}";
     * @return
     * @throws Exception
     * @Description : open-body-sig方式获取到Authorization 的值
     */
    public static String getOpenBodySigForNetpay(String appId, String appKey, String timestamp, String nonce, String content) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String bodyDigest = DigestUtils.sha256Hex(URLDecoder.decode(content, "UTF-8")); //eg:cc18f43baa87fe658146221d3f16e3b5a50d30f0c984407f74913ef6dcda8ee1
//        System.out.println(bodyDigest);
        String str1_C = appId + timestamp + nonce + bodyDigest;
        byte[] localSignature = hmacSHA256(str1_C.getBytes(), appKey.getBytes());
        String signature = Base64.encodeBase64String(localSignature);
        System.out.println(signature);
        return ("authorization=OPEN-FORM-PARAM" + "&appId=" + appId + "&timestamp=" + timestamp + "&nonce=" + nonce + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + URLEncoder.encode(signature, "UTF-8"));
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @Description
     */
    public static byte[] hmacSHA256(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }

}
