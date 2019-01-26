package com.chinatelecom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chinatelecom.request.Request;

public class OOSClient extends Request{
    
    Logger logger = LoggerFactory.getLogger(OOSClient.class);
    
    private static final int CONN_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    private static final String DATE_STR = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(
            DATE_STR, Locale.ENGLISH);
    static {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        DATE_FMT.setTimeZone(gmt);
    }
    private String host = "oos-js.ctyunapi.cn";
    private int port = 80;
    private static final String ak = "6fc56a43ed2b551b94c6";
    private static final String sk = "8d33af6fdd78361f9ef07d2f54522e6ea374cdc9";
    
    private URL url;
    private HttpURLConnection conn;
    
    private int responseCode = 0;
    private String responseHeader = "";
    private String responseBody = "";
    private JSONObject response = new JSONObject();
    
    private OOSClient() throws IOException {
        
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public OOSClient connect() throws Exception {
        String date = DATE_FMT.format(new Date());
        OutputStream output = null;
        if(objName!=null&&objName.equalsIgnoreCase("")) {
            url = new URL("http", host, port, "/" + bucketName + requestUrl);
        }else {
            url = new URL("http", host, port, "/" + bucketName + "/" + objName + requestUrl);
        }
        conn = (HttpURLConnection) url.openConnection();
        String authorization = authorize(requestMethod, date, bucketName, objName, requestUrl, beCanonicalizedAMZHeaders());
        logger.debug(authorization);
        conn.setRequestProperty("Authorization", authorization);
        conn.setRequestProperty("Date", date);
        conn.setConnectTimeout(CONN_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestMethod(requestMethod);
        conn.setDoOutput(true);
        for(String key: conn.getRequestProperties().keySet()) {
            logger.info(key+":"+conn.getRequestProperties().get(key));
        }
        conn.connect();
        if(!getRequestBody().isEmpty()) {
            try {
                output = conn.getOutputStream();
                logger.info(getRequestBody());
                output.write(getRequestBody().getBytes());
                output.flush();
            }finally {
                if(output != null) {
                    output.close();
                }
            }
        }
        getResponse(conn);
        return this;
    }
    
    private String beCanonicalizedAMZHeaders() {
        StringBuilder sb = new StringBuilder();
        for(String key: headers.keySet()) {
            if(key.startsWith("x-amz")) {
                sb.append(key+":"+headers.get(key)+"\n");
            }
            logger.info(key+":"+headers.get(key)+"\n");
            conn.setRequestProperty(key, headers.get(key));
        }
        
        return sb.toString();
    }
    
    public static OOSClient getClient() throws IOException {
        OOSClient client = new OOSClient();
        return client;
    }
    
    private String authorize(String httpVerb, String date, String bucket,
            String objectName, String requestUrl, String CanonicalizedAMZHeaders) throws Exception {
        String stringToSign;
        String ContentMD5 = "";
        if(headers.containsKey("Content-MD5")) {
            ContentMD5 = headers.get("Content-MD5");
        }
        if(objectName.equalsIgnoreCase("")) {
            stringToSign = httpVerb + "\n"+ContentMD5+"\n\n" + date + "\n" + CanonicalizedAMZHeaders + "/" + bucket + requestUrl;
        }else {
            stringToSign = httpVerb + "\n"+ContentMD5+"\n\n" + date + "\n" + CanonicalizedAMZHeaders + "/" + bucket + "/"
                    + objectName + requestUrl;
        }
        logger.debug(stringToSign);
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sk.getBytes("UTF-8"), "HmacSHA1"));
        byte[] macResult = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(macResult), "UTF-8");
        String authorization = "AWS " + ak + ":" + signature;
        return authorization;
    }
    
    public String sendRequest(Request request) throws Exception {
        init(request);
        connect();
        return response.toJSONString();
    }
    
    private String getResponse(HttpURLConnection conn) throws IOException {
        StringBuilder sb = new StringBuilder();
        responseCode = conn.getResponseCode();
        Map<String, List<String>> header = conn.getHeaderFields();
        for(String key: header.keySet()) {
            sb.append("----"+key+"       ");
            List<String> list = header.get(key);
            for(String s: list) {
                sb.append(s+"\n");
            }
        }
        responseHeader = sb.toString();
        sb = new StringBuilder();
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[1024 * 8];
        try {
            while (in.read(buffer) != -1) {
                sb.append(new String(buffer));
            }
        }finally {
            if(in != null) {
                in.close();
            }
        }
        responseBody = sb.toString();
        response.put("code", responseCode+"");
        response.put("header", responseHeader);
        response.put("body", responseBody);
        return sb.toString();
    }
    
    public int getResponseCode() {
        return responseCode;
    }
    
    public String getResponseHeader() {
        return responseHeader;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    
    
}
