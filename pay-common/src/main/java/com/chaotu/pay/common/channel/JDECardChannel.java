package com.chaotu.pay.common.channel;

import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.channel.ecard.SignUtils;
import com.chaotu.pay.common.channel.util.SignUtil;
import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;
import com.chaotu.pay.po.TChannel;
import com.chaotu.pay.po.TChannelAccount;
import com.chaotu.pay.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class JDECardChannel extends AbstractChannel {


    private static final String successStr = "success";

    public JDECardChannel(TChannel channel, TChannelAccount account) {
        super(channel, account);
    }

    @Override
    public String createSign(Map<String, Object> order) {
        return null;
    }

    @Override
    public String createNotifySign(Map<String, Object> signParam, HttpServletRequest request) {

        return "";
    }

    @Override
    public boolean checkNotify(Map<String, Object> signParam, HttpServletRequest request) {

        Map parameterMap = RequestUtil.getParameterMap(request);
        TreeMap<String, Object> treeMap = new TreeMap(parameterMap);

        String sign = treeMap.remove("sign").toString();
        log.info("sign: {}, params: {}", sign, JSONObject.toJSONString(treeMap));
        if (StringUtils.endsWithIgnoreCase(DigestUtil.createSignBySortMapForDianfei(treeMap, getAccount().getSignKey()), sign)) {
            return true;
        }
        return false;

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
        TreeMap<String,String> signParams = new TreeMap<>();
        signParams.put("money", order.getAmount().setScale(2).toString());
        signParams.put("pid", getAccount().getAccount());

        signParams.put("out_trade_no", order.getOrderNo());
        signParams.put("channel", getChannel().getChannelCode());
        signParams.put("type", "alipay");
        signParams.put("return_url", "http://www.baidu.com");
        signParams.put("mode", "json");
        signParams.put("name", "电费");
        String notifyUrl = getChannel().getNotifyUrl() + getChannel().getId() + "/" + order.getOrderNo();//回调地址
        //使用 RSA 1024 非对称加密公钥私钥对生成签名 签名字段进行键名字母排序(同步通知 notify_url;异步通知 async_notify_url 无需加入签名字段)，
        // 其他 content 请求参数 都需进行生成商户 sign 与服务端 sign 进行匹配
        signParams.put("notify_url", notifyUrl);
        signParams.put("sitename", "电费");
        signParams.put("sign", DigestUtil.createSignBySortMapForDianfei(signParams, getAccount().getSignKey()));

        System.out.println("params:"+ signParams);

        System.out.println("requestParams:" + JSONObject.toJSONString(signParams));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>(getChannel().getRequestUrl(), RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();

        if (StringUtils.equalsIgnoreCase(resMap.get("code").toString(), "200")) {
            SortedMap<String, Object> result = new TreeMap<>();
            result.put("userId", order.getUserId());
            result.put("amount", order.getAmount());
            result.put("qrCode", resMap.get("pay_url").toString());
            result.put("success","1");
            result.put("underOrderNo", order.getUnderOrderNo());
            result.put("orderNo", order.getOrderNo());
            result.put("upperOrderNo", "1");
            String resultSign = DigestUtil.createSignBySortMap(result, order.getUserKey());
            result.put("sign", resultSign);
            return result;
        }
        return null;

    }

    private boolean checkSign(SortedMap<Object, Object> params, String sign) {
        return StringUtils.equals(DigestUtil.createSign(params, getAccount().getSignKey()), sign);
    }

    private String createSign(SortedMap<String, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(key); //KEY是商户秘钥
        String sign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        return sign; // D3A5D13E7838E1D453F4F2EA526C4766
    }
}
