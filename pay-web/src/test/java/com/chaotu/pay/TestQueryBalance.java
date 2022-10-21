package com.chaotu.pay;

import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TestQueryBalance {

     //查询商户余额功能
    public static void main(String[] args) {
        SortedMap<String, Object> signParams = new TreeMap<>();
        signParams.put("mchid", "220960623");
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMapForDianfei(signParams, "6zab6gy3kqflg7bbebw1c7y3dh2pjrbq"));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>("http://wed.tongbao100.top/Payment_Dfpay_balance.html", RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();
       System.out.println("resMap -- " +resMap);
    }
}
