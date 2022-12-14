package com.chaotu.pay.common.channel;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;
import com.chaotu.pay.constant.CommonConstant;
import com.chaotu.pay.po.TChannel;
import com.chaotu.pay.po.TChannelAccount;
import com.chaotu.pay.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class WukongChannel extends AbstractChannel {


    private static final String successStr = "OK";

    public WukongChannel(TChannel channel, TChannelAccount account) {
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
        if (StringUtils.endsWithIgnoreCase(DigestUtil.createSignBySortMap(treeMap, getAccount().getSignKey()), sign)) {
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
        signParams.put("pay_amount", order.getAmount().setScale(2).toString());
        signParams.put("pay_memberid", getAccount().getAccount());

        signParams.put("pay_orderid", order.getOrderNo());
        signParams.put("pay_applydate", DateUtil.now());
        signParams.put("pay_bankcode", "1035");
        signParams.put("pay_callbackurl", "http://www.baidu.com");
        String notifyUrl = getChannel().getNotifyUrl() + getChannel().getId() + "/" + order.getOrderNo();//????????????
        //?????? RSA 1024 ?????????????????????????????????????????? ????????????????????????????????????(???????????? notify_url;???????????? async_notify_url ????????????????????????)???
        // ?????? content ???????????? ???????????????????????? sign ???????????? sign ????????????
        signParams.put("pay_notifyurl", notifyUrl);
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMap(signParams, getAccount().getSignKey()).toUpperCase());
        signParams.put("url",getChannel().getRequestUrl());
        signParams.put("pay_productname", "test");

        System.out.println("params:"+ signParams);

        System.out.println("requestParams:" + JSONObject.toJSONString(signParams));
        String postParamStr = RequestUtil.createPostParamStr(signParams);
        String url= CommonConstant.REDIRECT_URL+postParamStr;
        SortedMap<String, Object> result = new TreeMap<>();
        result.put("userId", order.getUserId());
        result.put("amount", order.getAmount());
        result.put("qrCode", url);
        result.put("success","1");
        result.put("underOrderNo", order.getUnderOrderNo());
        result.put("orderNo", order.getOrderNo());
        result.put("upperOrderNo", "1");
        String resultSign = DigestUtil.createSignBySortMap(result, order.getUserKey());
        result.put("sign", resultSign);
        return result;

    }

    private boolean checkSign(SortedMap<Object, Object> params, String sign) {
        return StringUtils.equals(DigestUtil.createSign(params, getAccount().getSignKey()), sign);
    }

    private String createSign(SortedMap<String, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();// ?????????????????????????????????accsii??????????????????
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
        sb.append(key); //KEY???????????????
        String sign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        return sign; // D3A5D13E7838E1D453F4F2EA526C4766
    }
}
