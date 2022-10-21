package com.chaotu.pay.common.channel;

import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DateUtil;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;
import com.chaotu.pay.po.TChannel;
import com.chaotu.pay.po.TChannelAccount;
import com.chaotu.pay.vo.DistributionVo;
import com.chaotu.pay.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class TongBao100Channel extends AbstractChannel {

    public TongBao100Channel(TChannel channel, TChannelAccount account) {
        super(channel, account);
    }

    @Override
    public String createSign(Map<String, Object> signParam) {
        return null;
    }

    @Override
    public String createNotifySign(Map<String, Object> signParam, HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean checkNotify(Map<String, Object> signParam, HttpServletRequest request) {
        String memberId = request.getParameter("memberid");
        String orderId = request.getParameter("orderid");
        String amount = request.getParameter("amount");
        String transaction_id = request.getParameter("transaction_id");
        String datetime = request.getParameter("datetime");
        String returncode = request.getParameter("returncode");
        String attach = request.getParameter("attach");
        String sign = request.getParameter("sign");
        SortedMap<String,Object> signParams = new TreeMap<>();
        signParams.put("amount",amount);
        signParams.put("datetime",datetime);
        signParams.put("memberId",memberId);
        signParams.put("orderId",orderId);
        signParams.put("transaction_id",transaction_id);
        signParams.put("returncode",returncode);
        String md5Str = DigestUtil.createSignBySortMap(signParams, getAccount().getSignKey()).toUpperCase();
        if(md5Str.equals(sign)){
            return true;
        }
        return true;
       // return false;
    }

    @Override
    public String getSuccessNotifyStr() {
        return null;
    }

    @Override
    Map<String, Object> createSignMap(OrderVo orderVo) {
        return null;
    }

    @Override
    public Object requestUpper(OrderVo order, String sign) {
        TreeMap<String, Object> signParams = new TreeMap<>();
        signParams.put("pay_amount", order.getAmount().longValue());
        signParams.put("pay_applydate", DateUtil.getDateTime(new Date()));
        signParams.put("pay_bankcode", getChannel().getChannelCode());
        String notifyUrl = getChannel().getNotifyUrl() + getChannel().getId() + "/" + order.getOrderNo();//回调地址

        signParams.put("pay_callbackurl", notifyUrl);
        signParams.put("pay_memberid", getAccount().getAccount());
        //使用 RSA 1024 非对称加密公钥私钥对生成签名 签名字段进行键名字母排序(同步通知 notify_url;异步通知 async_notify_url 无需加入签名字段)，
        // 其他 content 请求参数 都需进行生成商户 sign 与服务端 sign 进行匹配
        signParams.put("pay_notifyurl", notifyUrl);
        signParams.put("pay_orderid", order.getUnderOrderNo());
        //signParams.put("key", getAccount().getSignKey());
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMap(signParams, getAccount().getSignKey()).toUpperCase());
        signParams.put("pay_productname", "vip product service");
        System.out.println("params:" + signParams);
        System.out.println("requestParams:" + JSONObject.toJSONString(signParams));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>(getChannel().getRequestUrl(), RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();
        if (StringUtils.equalsIgnoreCase(resMap.get("code").toString(), "200")) {
            SortedMap<String, Object> result = new TreeMap<>();
            result.put("userId", order.getUserId());
            result.put("amount", order.getAmount());
            result.put("qrCode", resMap.get("data").toString());
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

    public Map queryBalance(TChannel channel, TChannelAccount account) {

        SortedMap<String, Object> signParams = new TreeMap<>();
        signParams.put("mchid", channel.getMerchant());
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMapForDianfei(signParams, getAccount().getSignKey()));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>("http://wed.tongbao100.top/Payment_Dfpay_balance.html", RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();
        log.info("tt-v", resMap);
        if (StringUtils.equalsIgnoreCase(resMap.get("status").toString(), "success")) {

        } else {
            log.info("error", resMap);
        }
        return resMap;
    }

    public void daifuPay(TChannel channel, TChannelAccount account, DistributionVo vo) {
        String merchant = channel.getMerchant();
        String orderNo = "211211";
        SortedMap<String, Object> signParams = new TreeMap<>();
        signParams.put("accountname", vo.getAccountname());    //开户名
        signParams.put("bankname", vo.getBankname());     //开户行名称
        signParams.put("cardnumber", vo.getBankcardno()); //银行卡号
        signParams.put("city", "");
        signParams.put("mchid", channel.getMerchant());
        signParams.put("money", vo.getToamount());
        signParams.put("out_trade_no", vo.getOutorderid());
        signParams.put("province", "");
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMapForDianfei(signParams, getAccount().getSignKey()));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>("http://wed.tongbao100.top/Payment_Dfpay_add.html", RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();
        log.info("tt-v", resMap);

    }
}