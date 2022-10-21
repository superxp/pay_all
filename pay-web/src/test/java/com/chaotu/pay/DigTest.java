package com.chaotu.pay;

import org.apache.commons.codec.digest.DigestUtils;

public class DigTest {
    public static void main(String[] args) {
       String hex = DigestUtils.md5Hex("amount=10&merchant=202210152121017339820&notifyurl=https://pay.xxx.com/demo/server.php&payTypeId=6&underOrderNo=20221021152115325368&key=e8152385964c46bea0ddedf29a2956f3");
       System.out.println(hex);
    }
}
