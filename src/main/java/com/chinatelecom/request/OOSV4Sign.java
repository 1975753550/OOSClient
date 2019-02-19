package com.chinatelecom.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

import com.chinatelecom.demo.Bucket;
import com.chinatelecom.util.OOSClientConfig;
import static com.chinatelecom.util.OOSClientConfig.*;

public class OOSV4Sign {
	
	private static Logger logger = LoggerFactory.getLogger(OOSV4Sign.class);
    
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    
    private static final String SCHEME = "AWS4";
    private static final String TERMINATOR = "aws4_request";
    private static final String ALGORITHM = "HMAC-SHA256";

    static{
        TimeZone utc = TimeZone.getTimeZone("UTC");
        timeFormatter.setTimeZone(utc);
        dateFormatter.setTimeZone(utc);
    }
    
    public static void main(String[] args) throws Exception {
        new Bucket().getBucketACL("bgmhhj");
    }
    
    
    static String authorize(Request request, HttpUriRequest httpRequest, OOSClientConfig config) {
        request.putHeader("x-amz-date", request.getDateTimeStamp().toUpperCase());
        request.putHeader("x-amz-content-sha256", getHashedPayload(request));
        request.putHeader("content-length", String.valueOf(request.getRequestBody().getBytes().length));
        return getSignature(request, config);
    }
    
    private static String getSignature(Request request, OOSClientConfig config) {
        String stringToSign = getStringToSign(request);

        byte[] kSecret = (SCHEME + sk).getBytes();
        byte[] kDate = sign(request.getDateStamp(), kSecret, "HmacSHA256");
        byte[] kRegion = sign(config.getRegionName(), kDate, "HmacSHA256");
        byte[] kService = sign(config.getServiceName(), kRegion, "HmacSHA256");
        byte[] kSigning = sign(TERMINATOR, kService, "HmacSHA256");
        byte[] signature = sign(stringToSign, kSigning, "HmacSHA256");
        
        
        String canonicalizedHeaderNames = getSignedHeaders(request);
        String scope =  request.getDateStamp() + "/" + request.getRegionName() + "/" + request.getServiceName() + "/" + TERMINATOR;
        String credentialsAuthorizationHeader = "Credential=" + ak + "/" + scope;
        String signedHeadersAuthorizationHeader = "SignedHeaders=" + canonicalizedHeaderNames;
        String signatureAuthorizationHeader = "Signature=" + toHex(signature);
        String authorizationHeader = SCHEME + "-" + ALGORITHM + " "
                + credentialsAuthorizationHeader + ", "
                + signedHeadersAuthorizationHeader + ", "
                + signatureAuthorizationHeader;
        return authorizationHeader;
    }
    
    private static String getStringToSign(Request request) {
        String scope =  request.getDateStamp() + "/" + request.getRegionName() + "/" + request.getServiceName() + "/" + TERMINATOR;
        String canonicalRequest = getCanonicalRequest(request);
        String stringToSign = SCHEME+"-"+ALGORITHM+"\n"+
                request.getDateTimeStamp() + "\n" +
                scope + "\n" +
                toHex(hash(canonicalRequest));
        if(logger.isDebugEnabled()) {
        	logger.debug("--------- String to sign -----------");
        	logger.debug(stringToSign);
        	logger.debug("------------------------------------");
        }
        return stringToSign;
    }
    
    private static String getCanonicalRequest(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestMethod());
        sb.append("\n");
        sb.append(getCanonicalURI(request));
        sb.append("\n");
        sb.append(getCanonicalQueryString(request));
        sb.append("\n");
        sb.append(getCanonicalHeaders(request));
        sb.append("\n");
        sb.append(getSignedHeaders(request));
        sb.append("\n");
        sb.append(getHashedPayload(request));if(logger.isDebugEnabled()) {
        	logger.debug("--------- Canonical request --------");
        	logger.debug(sb.toString());
        	logger.debug("------------------------------------");
        }
        return sb.toString();
    }
    
    private static String getCanonicalURI(Request request) {
        StringBuilder sb = new StringBuilder();
        if(!request.getObjName().isEmpty()) {
            sb.append("/");
            sb.append(request.getObjName());
        }else {
            sb.append("/");
        }
        return sb.toString();
    }
    
    private static String getCanonicalQueryString(Request request) {
        String requestUrl = request.getRequestUrl();
        StringBuilder sb = new StringBuilder();
        if(requestUrl.contains("?")) {
            Map<String, String> map = new TreeMap<String, String>();
            String queryString = requestUrl.substring(requestUrl.indexOf("?")+1);
            String[] query = queryString.split("&");
            for(String s: query) {
                String[] entry = s.split("=");
                if(entry.length>=2) {
                    map.put(urlEncode(entry[0], false), urlEncode(entry[1], false));
                }else {
                    map.put(urlEncode(entry[0], false), "");
                }
            }
            Iterator<Entry<String, String>> it = map.entrySet().iterator();
            while(it.hasNext()) {
                Entry<String, String> e = it.next();
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                if(it.hasNext()) {
                    sb.append("&");
                }
            }
        }else {
            sb.append("");
        }
        return sb.toString();
    }
    
    private static String getCanonicalHeaders(Request request) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> header = new TreeMap<String, String>();
        for(Entry<String, String> e: request.getHeaders().entrySet()) {
            header.put(e.getKey().toLowerCase(), e.getValue().trim());
        }
        for(Entry<String, String> e: header.entrySet()) {
            sb.append(e.getKey());
            sb.append(":");
            sb.append(e.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private static String getSignedHeaders(Request request) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> header = new TreeMap<String, String>();
        for(Entry<String, String> e: request.getHeaders().entrySet()) {
            header.put(e.getKey().toLowerCase(), e.getValue().trim().toLowerCase());
        }
        Iterator<String> it = header.keySet().iterator();
        while(it.hasNext()) {
            String s = it.next();
            sb.append(s);
            if(it.hasNext()) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    private static String getHashedPayload(Request request) {
        String payLoad = request.getRequestBody();
        return toHex(hash(payLoad));
    }
    
    
    private static String urlEncode(String url, boolean keepPathSlash) {
        String encoded;
        try {
            encoded = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding is not supported.", e);
        }
        if ( keepPathSlash ) {
            encoded = encoded.replace("%2F", "/");
        }
        return encoded;
    }
    
    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256
     * algorithm.
     */
    private static byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("UTF-8"));
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data
     *            data to hex encode.
     *
     * @return hex-encoded string.
     */
    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }
    
    public static byte[] sign(String stringData, byte[] key, String algorithm) {
        try {
            byte[] data = stringData.getBytes("UTF-8");
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

}
