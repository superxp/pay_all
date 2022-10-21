package com.chaotu.pay.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WXMyConfigUtil extends WXPayConfig {

    private byte[] certData;

    public WXMyConfigUtil() throws Exception {
        String certPath = "证书地址";//从微信商户平台下载的安全证书存放的目录

        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
        return "填写appid";
    }

    //parnerid
    @Override
    public String getMchID() {
        return "填写商户id";
    }

    @Override
    public String getKey() {
        return "填写api密钥";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return null;
    }
}
