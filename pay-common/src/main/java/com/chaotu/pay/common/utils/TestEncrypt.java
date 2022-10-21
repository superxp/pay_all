package com.chaotu.pay.common.utils;

import java.io.*;
import java.lang.reflect.Method;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

import javax.crypto.Cipher;

public class TestEncrypt {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        PublicKey pub=getPublicKey("/Users/cqx/rsa_public_key_2048.pem","RSA");
        System.out.println("hahhahah11");
        String str="我是需要传递的字符串";
        PrivateKey pri= getPrivateKey("/Users/cqx/bh_pkcs8_rsa_private_key_2048.pem","RSA");
        byte[] estr=encrypt(str.getBytes(),pub,2048, 11,"RSA/ECB/PKCS1Padding");
        System.out.println(new String(estr));
        System.out.println("hahhahah12");
        byte[] dstr=decrypt(estr, pri, 2048, 11, "RSA/ECB/PKCS1Padding");
        System.out.println(new String(dstr));

    }
    public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8;
        int decryptBlockSize = keyByteSize - reserveSize;
        int nBlock = encryptedBytes.length / keyByteSize;
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                outbuf.write(decryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            throw new Exception("DEENCRYPT ERROR:", e);
        } finally {
            try{
                if(outbuf != null){
                    outbuf.close();
                }
            }catch (Exception e){
                outbuf = null;
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }
    public static byte[] encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm)  {
        int keyByteSize = keyLength / 8;
        int encryptBlockSize = keyByteSize - reserveSize;
        int nBlock = plainBytes.length / encryptBlockSize;
        if ((plainBytes.length % encryptBlockSize) != 0) {
            nBlock += 1;
        }
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(outbuf != null){
                    outbuf.close();
                }
            }catch (Exception e){
                outbuf = null;
               e.printStackTrace();
            }
            return null;
        }
    }

    public static PublicKey getPublicKey(String pubKey, String keyAlgorithm)  {
        try {

            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(decodeBase64(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PublicKey publicKey = keyFactory.generatePublic(pubX509);

            return publicKey;
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            return null;
        }
    }
    public static String getMd5Sign(String content , PrivateKey privateKey) {

        try {
            byte[] contentBytes = content.getBytes("utf-8");
            Signature signature = null;
            signature = Signature.getInstance("MD5withRSA");
            signature.initSign(privateKey);
            signature.update(contentBytes);
            byte[] signs = signature.sign();
            return encodeBase64(signs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static PrivateKey getPrivateKey(String priKey, String keyAlgorithm) {
        try {
            System.out.println("hahhah4!"+decodeBase64(priKey));
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decodeBase64(priKey));
            System.out.println("hahhah5!");
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            System.out.println("hahhah6!");
            PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);
            System.out.println("hahhah7!");
            return privateKey;
        } catch (Exception e) {
           e.printStackTrace();
        }  finally {
            return null;
        }
    }
    //一下面是base64的编码和解码
    public static String encodeBase64(byte[]input) throws Exception{
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod= clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj=mainMethod.invoke(null, new Object[]{input});
        return (String)retObj;
    }
    /***
     * decode by Base64
     */
    public static byte[] decodeBase64(String input) throws Exception{
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod= clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj=mainMethod.invoke(null, input);
        return (byte[])retObj;
    }
}