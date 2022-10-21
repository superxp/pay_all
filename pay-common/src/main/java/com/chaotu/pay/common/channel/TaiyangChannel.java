package com.chaotu.pay.common.channel;

import com.alibaba.fastjson.JSONObject;
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
public class TaiyangChannel extends AbstractChannel {


    private static final String successStr = "success";

    public TaiyangChannel(TChannel channel, TChannelAccount account) {
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
        String status = request.getParameter("orderStatus");

        Map parameterMap = RequestUtil.getParameterMap(request);

        String sign = parameterMap.remove("sign").toString();
        log.info("通道:" + getChannel().getChannelName() + "接收回调验证：" + status);
        if ("1".equals(status) && StringUtils.equals(sign,DigestUtil.createSign(new TreeMap<>(parameterMap),getAccount().getSignKey()).toUpperCase()))
            return true;

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
        String merchantNo = getAccount().getAccount();//商户id
        String merchantSn = order.getOrderNo();//20位订单号 时间戳+6位随机字符串组成
         String channel = getChannel().getChannelCode();//通知地址
        String notifyUrl = getChannel().getNotifyUrl() + getChannel().getId() + "/" + merchantSn;//回调地址
        String amount = order.getAmount().toString();
        SortedMap<Object, Object> map = new TreeMap<>();
        map.put("merchantNo", merchantNo);
        map.put("merchantSn", merchantSn);
        map.put("channel", channel);
        map.put("notifyUrl", notifyUrl);
        map.put("amount", amount);
        map.put("time", System.currentTimeMillis()/1000);
        map.put("signType", "md5");
        map.put("sign", DigestUtil.createSign(map,getAccount().getSignKey()).toUpperCase());
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>(getChannel().getRequestUrl(), RequestUtil.createPostParamStr(map), null);
         Map<String, Object> resMap = paySender.send();

        if (StringUtils.equalsIgnoreCase(resMap.get("code").toString(), "200")) {
            JSONObject data = (JSONObject) resMap.get("data");
            SortedMap<String, Object> result = new TreeMap<>();
            result.put("userId", order.getUserId());
            result.put("amount", order.getAmount());
            result.put("qrCode", data.getString("payUrl"));
            result.put("success", "1");
            result.put("underOrderNo", order.getUnderOrderNo());
            result.put("orderNo", order.getOrderNo());
            result.put("upperOrderNo", data.getString("sn"));
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
