package com.chaotu.pay.common.channel;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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
public class YouKaNewChannel extends AbstractChannel {


    private static final String successStr = "success";

    public YouKaNewChannel(TChannel channel, TChannelAccount account) {
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
        String business_code = request.getParameter("business_code");
        String out_trade_no  = request.getParameter("out_trade_no");
        String plus_order_no = request.getParameter("plus_order_no");
        String pay_amount   = request.getParameter("pay_amount");
        String cheap_amount = request.getParameter("cheap_amount");
        String trade_status = request.getParameter("status");
        TreeMap<Object, Object> treeMap = new TreeMap<>();
        treeMap.put("business_code",business_code);
        treeMap.put("out_trade_no" ,out_trade_no );
        treeMap.put("plus_order_no",plus_order_no);
        treeMap.put("pay_amount"   ,pay_amount   );
        treeMap.put("cheap_amount" ,cheap_amount );
        treeMap.put("trade_status" ,trade_status );
        Map parameterMap = RequestUtil.getParameterMap(request);

        String sign = parameterMap.remove("sign").toString();
        log.info("通道:" + getChannel().getChannelName() + "接收回调验证：" + parameterMap);
        if ("1".equals(trade_status) && StringUtils.equals(sign,DigestUtil.createSign(new TreeMap<>(parameterMap),getAccount().getSignKey()).toUpperCase()))
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
        String business_code = getAccount().getAccount();//商户id
        String out_trade_no = order.getOrderNo();//20位订单号 时间戳+6位随机字符串组成
        String channel_type = getChannel().getChannelCode();//通知地址
        String notify_url = getChannel().getNotifyUrl() + getChannel().getId() + "/" + out_trade_no;//回调地址
        int amount = order.getAmount().intValue();
        String goods_name = "充值";
        SortedMap<Object, Object> map = new TreeMap<>();
        map.put("memberId", business_code);
        map.put("orderId", out_trade_no);
        map.put("payType", channel_type);
        map.put("notifyUrl", notify_url);
        map.put("ip", "127.0.0.1");
        map.put("amount", amount);
        map.put("date", DateUtil.now());
        map.put("sign", DigestUtil.createSign(map,getAccount().getSignKey()).toUpperCase());
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>(getChannel().getRequestUrl(), RequestUtil.createPostParamStr(map), null);
         Map<String, Object> resMap = paySender.send();

        if (StringUtils.equalsIgnoreCase(resMap.get("status").toString(), "200")) {
            SortedMap<String, Object> result = new TreeMap<>();
            result.put("userId", order.getUserId());
            result.put("amount", order.getAmount());
            result.put("qrCode", resMap.get("data"));
            result.put("success", "1");
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
