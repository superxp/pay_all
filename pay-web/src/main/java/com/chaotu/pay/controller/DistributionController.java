package com.chaotu.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.chaotu.pay.common.channel.qrcpay.util.Common;
import com.chaotu.pay.common.channel.qrcpay.util.RSAUtils2;
import com.chaotu.pay.common.sender.PddMerchantSender;
import com.chaotu.pay.common.utils.IDGeneratorUtils;
import com.chaotu.pay.common.utils.ResponseUtil;
import com.chaotu.pay.po.TDistribution;
import com.chaotu.pay.po.TDistribution;
import com.chaotu.pay.qo.DistributionQo;
import com.chaotu.pay.qo.WithdrawsQo;
import com.chaotu.pay.service.DistributionService;
import com.chaotu.pay.service.WithdrawsService;
import com.chaotu.pay.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 结算管理
 * @author: chenyupeng
 * @create: 2019-04-19 10:35
 **/
@Slf4j
@RestController
@RequestMapping("/distribution")
public class DistributionController {
    @Autowired
    private DistributionService withdrawsService;
    private static final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc7luX7cmA256qukMnFFRWgmHifHjTaRDMa5jvAJ7qdU9AmdISrECDlFHIpVkYLLAB7j8qgqrh9q+nuE/1WIo83yD9vpLDsgiE+zUZ9xHmeLjvoDXmKkHhPSBsNH9RYrWnrn6Ru1oglfYcTAnwHYiISEzp3Yrx/eEbTbar9K1ZiQIDAQAB";
    private static final String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANzuW5ftyYDbnqq6QycUVFaCYeJ8eNNpEMxrmO8Anup1T0CZ0hKsQIOUUcilWRgssAHuPyqCquH2r6e4T/VYijzfIP2+ksOyCIT7NRn3EeZ4uO+gNeYqQeE9IGw0f1FitaeufpG7WiCV9hxMCfAdiIhITOndivH94RtNtqv0rVmJAgMBAAECgYAvQmGfb0MyaEhZlvx0aJnd44gSzbN+7bOlNsMBJS3ZU3y/gef5DJXET77q38veKvj/gLpMWqU5Lu7GPtwDzIvNMeLbYqS5TDQS44gxlRrjn81AXVBzX3xjjh6XFRR1r2QctJs7s/MiW5DEKf5j4Q2joqEU2URKP7mtvcRvsktPAQJBAO3xp3ZTxQ7l039EYgPMFge7M401z4l1wVSYh7ITKMa3W0T+7QJHwn7yydm33Oea7Tb7eZgOU9UcMUc0iEmKTWkCQQDtsjVLOHoCag4qYq7VJt3sb/QpVjsdV10ODeyWOcfwTsp48s87JbfwbOS4lBvgYm01pO/kBpHZ4h+FlTtW74chAkEAhjnuB/gVj+PiPUbsK8wzGUVnPV9/pcGBwCETW0cnl4HTwMY2GTU16Ls5VtI7kYN6Eawm2borXGq8+bgOsb2NEQJBALwKTUrypOMgD5DMfM83bj1L2/aPtzhhEsa5kT7O+zNKwbapL/P0xO042ECFOwBqHUdg8j6MS/n4f0NoaYc++sECQHFP64TF2oAbhYlI7AxVqAIm08uoLVpFjxwvEIkHdT7c99B8HklLvswbg2X1bcoH9QDhsViOYiIJ77k9OgmeHfY=";


    /**
     * 多条件分页获取订单列表
     * @return
     */
    @PostMapping("/all")
    public Message getAllAgent(@RequestBody DistributionQo withdrawsQo){

        Map<String,Object> pageInfo = null;
        PageVo pageVo = withdrawsQo.getPageVo();
        SearchVo searchVo = withdrawsQo.getSearchVo();
        DistributionVo withdrawsVo = withdrawsQo.getDistributionVo();

        try {
            pageInfo = withdrawsService.findByCondition(pageVo,searchVo,withdrawsVo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ResponseUtil.responseBody(pageInfo);
    }
    /**
     * 插入结算申请
     * @return
     */
    @PostMapping("/add")
    public Message add(@RequestBody DistributionVo vo){


        if(StringUtils.isBlank(vo.getBankcardno())||vo.getWithdrawamount().equals(new BigDecimal(0)))
            return ResponseUtil.responseBody("-1", "参数有误！");
        try{
            withdrawsService.add(vo);
            sendRequest(vo);
        }catch (IllegalArgumentException e){
            return ResponseUtil.responseBody("-1", "余额不足！");
        }
        return ResponseUtil.responseBody("申请已提交!");
    }

    private void sendRequest(DistributionVo vo){
        Map<String, Object> map = new HashMap<String, Object>();

        String prikey= Common.PRIVATEKEY;

        String pubkey=Common.PUBLICKKEY;

        // 机构编号
        String agentnum = Common.AGENTNUM;
        // 商户编号
        String mercnum = "850121058460003";

        // ===========================代付请求
        String Code = "A0001"; // 业务编码
        map.put("agentNum", agentnum);
        map.put("mercNum", mercnum); // 商户编号
        map.put("amount", "5000"); // 金额（分）
        map.put("bankProvinceCity", "xx省-xx市"); // 开户省市
        map.put("banksysnumber", "xx"); // 联行号
/*        map.put("headBank", vo.getBankcode()); // 开户行
        map.put("branchBank", vo.getBranchname());// 开户支行
        map.put("settlementName", vo.getAccountname()); // 开户姓名
        map.put("cardNo", vo.getBankcardno()); // 卡号*/
        map.put("headBank", "招商银行"); // 开户行
        map.put("branchBank", "西安白沙路支行");// 开户支行
        map.put("settlementName", "赵立"); // 开户姓名
        map.put("cardNo","6214855252928318" ); // 卡号
        map.put("crpIdNo", "3701xxxxxxxxxxxxxx"); // 身份证号
        map.put("phone", "910290000420"); // 开户手机号
        map.put("orderNo","P"+ IDGeneratorUtils.getFlowNum()); // 订单号



        System.out.println("业务数据拼接完成|" + map + "|");

        String jsonMap = JSONObject.toJSONString(map);

        System.out.println("业务数据转json|" + jsonMap + "|");

        jsonMap = org.apache.commons.codec.binary.Base64.encodeBase64String(jsonMap.getBytes());

        System.out.println("业务数据json转base64|" + jsonMap + "|");

        //通过私钥进行签名，得到签名
        byte[] mySign = com.chaotu.pay.common.channel.qrcpay.util.LocalUtil.sign(
                org.apache.commons.codec.binary.Base64.decodeBase64(prikey), jsonMap);

        String reqSign = new String(mySign);

        System.out.println("数据签名|" + reqSign + "|");

        //通过公钥进行数据加密
        byte[] encodedData = new byte[0];
        try {
            encodedData = RSAUtils2.encryptByPublicKey(jsonMap.getBytes(),
                    pubkey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String reqData = org.apache.commons.codec.binary.Base64.encodeBase64String(encodedData);

        System.out.println("数据加密|" + reqData + "|");

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("sign", reqSign);
        request.put("data", reqData);
        request.put("agentNum", agentnum);
        System.out.println("上送业务数据|" + request + "|");

        JSONObject realParam = new JSONObject();
        realParam.put("code", Code);
        realParam.put("request", request);

        System.out.println("上送全部数据|" + realParam.toString() + "|");

        String reqParam = String.format("requestData=%s&unitType=paid", URLEncoder.encode(realParam.toJSONString()));


        PddMerchantSender<Map<String,Object>> paySender = new PddMerchantSender<>(Common.DAIFU_URL,reqParam,null);

         System.out.println("响应数据|" + paySender + "|");


        Map<String, Object> rspMap = paySender.send();

        System.out.println("响应数据转Map|" + rspMap + "|");

        JSONObject response = (JSONObject)rspMap.get("response");

        String resp = response.getString("resp");
        String rspCode = response.getString("respCode");
    }

    /**
     * 编辑结算申请
     * @return
     */
    @PostMapping("/update")
    public Message add(@RequestBody TDistribution vo){
        withdrawsService.update(vo);
        return ResponseUtil.responseBody("申请已提交!");
    }

    /**
     * 通过结算申请
     * @return
     */
    @PostMapping("/pass")
    public Message pass(@RequestBody TDistribution vo){
        withdrawsService.pass(vo);
        return ResponseUtil.responseBody("申请已结算!");
    }
}
