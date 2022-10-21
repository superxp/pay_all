package com.chaotu.pay;

import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.DigestUtil;
import com.chaotu.pay.common.utils.RequestUtil;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TestDaifu {

    public static void main(String[] args) {
        String merchant = "220960623";
        String orderNo = "211211";
        SortedMap<String, Object> signParams = new TreeMap<>();
        signParams.put("accountname", "段小胖");    //开户名
        signParams.put("bankname", "中国银行");     //开户行名称
        signParams.put("cardnumber", "6217582000006771001"); //银行卡号
        signParams.put("city", "深圳");
        signParams.put("mchid", merchant);
        signParams.put("money","");
        signParams.put("out_trade_no", orderNo);
        signParams.put("province", "广东");
        signParams.put("pay_md5sign", DigestUtil.createSignBySortMapForDianfei(signParams, "6zab6gy3kqflg7bbebw1c7y3dh2pjrbq"));
        PddMerchantSender<Map<String, Object>> paySender = new PddMerchantSender<>("http://wed.tongbao100.top/Payment_Dfpay_add.html", RequestUtil.createPostParamStr(signParams), null);
        Map<String, Object> resMap = paySender.send();
       System.out.println(resMap);
    }
}
