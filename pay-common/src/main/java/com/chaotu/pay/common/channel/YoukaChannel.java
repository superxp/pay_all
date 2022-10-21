package com.chaotu.pay.common.channel;

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
public class YoukaChannel extends AbstractChannel {


    private static final String successStr = "success";

    public YoukaChannel(TChannel channel, TChannelAccount account) {
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
        String trade_status = request.getParameter("trade_status");
        TreeMap<Object, Object> treeMap = new TreeMap<>();
        treeMap.put("business_code",business_code);
        treeMap.put("out_trade_no" ,out_trade_no );
        treeMap.put("plus_order_no",plus_order_no);
        treeMap.put("pay_amount"   ,pay_amount   );
        treeMap.put("cheap_amount" ,cheap_amount );
        treeMap.put("trade_status" ,trade_status );
        Map parameterMap = RequestUtil.getParameterMap(request);

        String sign = parameterMap.remove("sign").toString();
        log.info("通道:" + getChannel().getChannelName() + "接收回调验证：" + trade_status);
        if ("TRADE_SUCCESS".equals(trade_status) && StringUtils.equals(sign,DigestUtil.createSign(new TreeMap<>(parameterMap),getAccount().getSignKey())))
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
        String amount = order.getAmount().toString();
        String goods_name = "充值";
        SortedMap<Object, Object> map = new TreeMap<>();
        map.put("business_code", business_code);
        map.put("out_trade_no", out_trade_no);
        map.put("channel_type", channel_type);
        map.put("notify_url", notify_url);
        map.put("amount", amount);
        map.put("goods_name", goods_name);
        map.put("sign", DigestUtil.createSign(map,getAccount().getSignKey()));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>(getChannel().getRequestUrl(), RequestUtil.createPostParamStr(map), null);
         Map<String, Object> resMap = paySender.send();

        if (StringUtils.equalsIgnoreCase(resMap.get("code").toString(), "000000")) {
            SortedMap<String, Object> result = new TreeMap<>();
            Map data = (Map) resMap.get("data");
            result.put("userId", order.getUserId());
            result.put("amount", order.getAmount());
            result.put("qrCode", data.get("order_url"));
            result.put("success", "1");
            result.put("underOrderNo", order.getUnderOrderNo());
            result.put("orderNo", order.getOrderNo());
            result.put("upperOrderNo", data.get("plus_order_no"));
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
