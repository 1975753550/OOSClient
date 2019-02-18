package com.chinatelecom.request;

import static com.chinatelecom.util.OOSClientConfig.ak;
import static com.chinatelecom.util.OOSClientConfig.sk;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OOSV2Sign {
    
    private static Logger logger = LoggerFactory.getLogger(OOSV2Sign.class);
    
    public static String authorize(
            Request request,
            String CanonicalizedAMZHeaders, HttpUriRequest httpRequest)
            throws NoSuchAlgorithmException, IllegalStateException,
            UnsupportedEncodingException, InvalidKeyException {
        String stringToSign;
        String contentMD5 = "";
        String contentType = "";
        if (request.getRequestUrl().startsWith("?Action")) {
            request.setRequestUrl("");
        }
        if (request.getHeaders().containsKey("Content-MD5")) {
            contentMD5 = request.getHeaders().get("Content-MD5");
        }
        if (request.getHeaders().containsKey("Content-Type")) {
            contentType = request.getHeaders().get("Content-Type");
        }
        if (request.getObjName().equalsIgnoreCase("")) {
            stringToSign = request.getRequestMethod() + "\n" + contentMD5 + "\n" + contentType
                    + "\n" + request.getDate() + "\n" + CanonicalizedAMZHeaders + "/"
                    + request.getBucketName() + request.getRequestUrl();
        } else {
            stringToSign = request.getRequestMethod() + "\n" + contentMD5 + "\n" + contentType
                    + "\n" + request.getDate() + "\n" + CanonicalizedAMZHeaders + "/"
                    + request.getBucketName() + "/" + request.getObjName() + request.getRequestUrl();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(stringToSign);
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sk.getBytes("UTF-8"), "HmacSHA1"));
        byte[] macResult = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(macResult), "UTF-8");
        String authorization = "AWS " + ak + ":" + signature;
        httpRequest.setHeader("Date", request.getDate());
        return authorization;
    }
    
    public static void main(String[] args) throws Exception {
        String stringToSign = "GET\n" + 
                "\n" + 
                "\n" + 
                "Mon, 18 Feb 2019 07:40:09 GMT\n" + 
                "/bgm";
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sk.getBytes("UTF-8"), "HmacSHA1"));
        byte[] macResult = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(macResult), "UTF-8");
        String authorization = "AWS " + ak + ":" + signature;
        System.out.println(authorization);
    }
}
