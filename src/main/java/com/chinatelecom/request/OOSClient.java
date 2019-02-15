package com.chinatelecom.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chinatelecom.request.Request;
import com.chinatelecom.util.MyConnectionKeepAliveStrategy;
import com.chinatelecom.request.OOSV4Sign;

@Component
public class OOSClient {

    Logger logger = LoggerFactory.getLogger(OOSClient.class);

    private static final int CONN_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 30000;
    static String regionName = "hesjz";
    static String serviceName = "s3";
    private static String host = "oos-"+regionName+".ctyunapi.cn";
    private static int port = 80;
    static final String ak = "f4f0e26e5952be5b04b7";
    static final String sk = "21896471c739eff494895f2ec583b2039883e741";
    private static boolean isV4Signature = true;
    
    
    private ThreadLocal<Request> request = new ThreadLocal<Request>();
    private ThreadLocal<Integer> responseCode = new ThreadLocal<Integer>();
    private ThreadLocal<String> responseHeader = new ThreadLocal<String>();
    private ThreadLocal<String> responseBody = new ThreadLocal<String>();
    private ThreadLocal<HttpResponse> response = new ThreadLocal<HttpResponse>();
    private volatile static OOSClient client;
    private volatile static OOSClient IAMclient;
    private final HttpClient httpClient;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    private OOSClient() {

        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(200);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(200);
        HttpHost httpHost = new HttpHost(host, port);
        HttpRoute route = new HttpRoute(httpHost);
        poolingHttpClientConnectionManager.setMaxPerRoute(route, 100);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONN_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(READ_TIMEOUT).build();
        httpClient = HttpClientBuilder.create().setMaxConnTotal(200)
                .setMaxConnPerRoute(100).setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setKeepAliveStrategy(new MyConnectionKeepAliveStrategy())
                .build();
    }

    public OOSClient connect() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException {
        if (request.get().getObjName() != null
                && request.get().getObjName().equalsIgnoreCase("")) {
            request.get().setUrl(new URL("http", host, port, "/"
                    + request.get().getBucketName() + request.get().getRequestUrl()));
        } else {
            request.get().setUrl(new URL("http", host, port,
                    "/" + request.get().getBucketName() + "/"
                            + request.get().getObjName()
                            + request.get().getRequestUrl()));
        }
        HttpUriRequest httpRequest = null;
        switch (request.get().getRequestMethod()) {
        case "GET":
            httpRequest = new HttpGet(request.get().getUrl().toString());
            break;
        case "POST":
            httpRequest = new HttpPost(request.get().getUrl().toString());
            break;
        case "PUT":
            httpRequest = new HttpPut(request.get().getUrl().toString());
            break;
        case "DELETE":
            httpRequest = new HttpDelete(request.get().getUrl().toString());
            break;
        case "HEAD":
            httpRequest = new HttpHead(request.get().getUrl().toString());
            break;
        }
        String authorization;
        if(isV4Signature) {
            authorization = OOSV4Sign.authorize(request.get(), httpRequest);
        }else {
            authorization = authorize(request.get().getRequestMethod(), request.get().getDate(),
                    request.get().getBucketName(), request.get().getObjName(),
                    request.get().getRequestUrl(), beCanonicalizedAMZHeaders(httpRequest), 
                    httpRequest);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(authorization);
        }
        
        beCanonicalizedAMZHeaders(httpRequest);
        httpRequest.setHeader("Authorization", authorization);
        if (!request.get().getRequestBody().isEmpty()) {
            StringEntity entity = new StringEntity(
                    request.get().getRequestBody());
            if (httpRequest instanceof HttpPut) {
                ((HttpPut) httpRequest).setEntity(entity);
            }
            if (httpRequest instanceof HttpPost) {
                ((HttpPost) httpRequest).setEntity(entity);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(httpRequest.getURI().toString());
            for (Header head : httpRequest.getAllHeaders()) {
                logger.debug(head.getName() + ":" + head.getValue());
            }
        }

        response.set(httpClient.execute(httpRequest));
        getResponse(response.get());
        return this;
    }

    private String beCanonicalizedAMZHeaders(HttpUriRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        for (String key : request.get().getHeaders().keySet()) {
            if (key.startsWith("x-amz")) {
                sb.append(
                        key + ":" + request.get().getHeaders().get(key) + "\n");
            }
            if (logger.isDebugEnabled()) {
                logger.debug(
                        key + ":" + request.get().getHeaders().get(key) + "\n");
            }
            if(key.equals("content-length"))
                continue;
            httpRequest.setHeader(key, request.get().getHeaders().get(key));
        }

        return sb.toString();
    }

    public static OOSClient getClient() {
        if (client == null) {
            synchronized (OOSClient.class) {
                if (client == null) {
                    setHost("oos-"+regionName+".ctyunapi.cn");
                    client = new OOSClient();
                }
            }
        }
        return client;
    }

    public static OOSClient getIAMClient() {
        if (IAMclient == null) {
            synchronized (OOSClient.class) {
                if (IAMclient == null) {
                    setHost("oos-"+regionName+"-iam.ctyunapi.cn");
                    IAMclient = new OOSClient();
                }
            }
        }
        return IAMclient;
    }

    private String authorize(String httpVerb, String date, String bucket,
            String objectName, String requestUrl,
            String CanonicalizedAMZHeaders,
            HttpUriRequest httpRequest)
            throws NoSuchAlgorithmException, IllegalStateException,
            UnsupportedEncodingException, InvalidKeyException {
        String stringToSign;
        String contentMD5 = "";
        String contentType = "";
        if(requestUrl.startsWith("?Action")) {
            requestUrl = "";
        }
        if (request.get().getHeaders().containsKey("Content-MD5")) {
            contentMD5 = request.get().getHeaders().get("Content-MD5");
        }
        if (request.get().getHeaders().containsKey("Content-Type")) {
            contentType = request.get().getHeaders().get("Content-Type");
        }
        if (objectName.equalsIgnoreCase("")) {
            stringToSign = httpVerb + "\n" + contentMD5 + "\n" + contentType
                    + "\n" + date + "\n" + CanonicalizedAMZHeaders + "/"
                    + bucket + requestUrl;
        } else {
            stringToSign = httpVerb + "\n" + contentMD5 + "\n" + contentType
                    + "\n" + date + "\n" + CanonicalizedAMZHeaders + "/"
                    + bucket + "/" + objectName + requestUrl;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(stringToSign);
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sk.getBytes("UTF-8"), "HmacSHA1"));
        byte[] macResult = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(macResult), "UTF-8");
        String authorization = "AWS " + ak + ":" + signature;
        httpRequest.setHeader("Date", date);
        return authorization;
    }

    public String sendRequest(Request request) throws Exception {
        this.request.set(request);
        request.putHeader("host", host);
        request.setRegionName(regionName);
        request.setServiceName(serviceName);
        request.putHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//        request.putHeader("Connection", "Keep-Alive");
        request.putHeader("User-Agent", "Zw_Acoll");
        request.putHeader("Accept-Encoding", "gzip,deflate");
        connect();
        return String.valueOf(response.get().getStatusLine().getStatusCode());
    }

    private String getResponse(HttpResponse response) throws IOException {
        responseCode.set(response.getStatusLine().getStatusCode());
        if (logger.isDebugEnabled()) {
            logger.debug(response.getStatusLine().toString());
        }
        StringBuilder head = new StringBuilder();
        for (Header header : response.getAllHeaders()) {
            head.append(header.getName() + ":" + header.getValue() + "\n");
        }
        responseHeader.set(head.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(head.toString());
        }
        HttpEntity entity = response.getEntity();
        InputStream instream = null;
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024 * 8];
        try {
            if (entity != null) {
                instream = entity.getContent();
                int len;
                while ((len = instream.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, len));
                }
            }
        } finally {
            if (instream != null) {
                instream.close();
            }
        }
        responseBody.set(sb.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(responseBody.get());
        }
        return responseBody.get();
    }

    public int getResponseCode() {
        return responseCode.get();
    }

    public String getResponseHeader() {
        return responseHeader.get();
    }

    public String getResponseBody() {
        return responseBody.get();
    }

    public void setRequestUrl(String requestUrl) {
        request.get().setRequestUrl(requestUrl);
    }

    private static void setHost(String endPoint) {
        host = endPoint;
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void autosync() {
        if (poolingHttpClientConnectionManager != null) {
            if (logger.isInfoEnabled()) {
                logger.info("开始清除无效链接....");
            }
            poolingHttpClientConnectionManager.closeExpiredConnections();
            poolingHttpClientConnectionManager.closeIdleConnections(30,
                    TimeUnit.SECONDS);

        }
    }

}
