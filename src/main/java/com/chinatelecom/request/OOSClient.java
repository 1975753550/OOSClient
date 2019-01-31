package com.chinatelecom.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chinatelecom.request.Request;

@Component
public class OOSClient {

    Logger logger = LoggerFactory.getLogger(OOSClient.class);

    private static final int CONN_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 30000;
    private static final String DATE_STR = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(
            DATE_STR, Locale.ENGLISH);
    static {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        DATE_FMT.setTimeZone(gmt);
    }
    private static final String host = "oos-js.ctyunapi.cn";
    private static final int port = 80;
    private static final String ak = "6fc56a43ed2b551b94c6";
    private static final String sk = "8d33af6fdd78361f9ef07d2f54522e6ea374cdc9";

    private ThreadLocal<URL> url = new ThreadLocal<URL>();
    private ThreadLocal<Request> request = new ThreadLocal<Request>();

    private ThreadLocal<Integer> responseCode = new ThreadLocal<Integer>();
    private ThreadLocal<String> responseHeader = new ThreadLocal<String>();
    private ThreadLocal<String> responseBody = new ThreadLocal<String>();
    private ThreadLocal<HttpResponse> response = new ThreadLocal<HttpResponse>();
    private volatile static OOSClient client;
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
                .setConnectTimeout(CONN_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)  
                .setSocketTimeout(READ_TIMEOUT).build(); 
        httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(100)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }

    public OOSClient connect() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException {
        String date;
        synchronized (OOSClient.class) {
            date = DATE_FMT.format(new Date());
        }
        if (request.get().getObjName() != null
                && request.get().getObjName().equalsIgnoreCase("")) {
            url.set(new URL("http", host, port, "/"
                    + request.get().getBucketName() + request.get().getUrl()));
        } else {
            url.set(new URL("http", host, port,
                    "/" + request.get().getBucketName() + "/"
                            + request.get().getObjName()
                            + request.get().getUrl()));
        }
        HttpUriRequest httpRequest = null;
        HttpContext localContext = new BasicHttpContext();
        switch (request.get().getRequestMethod()) {
        case "GET":
            httpRequest = new HttpGet(url.get().toString());
            break;
        case "POST":
            httpRequest = new HttpPost(url.get().toString());
            break;
        case "PUT":
            httpRequest = new HttpPut(url.get().toString());
            break;
        case "DELETE":
            httpRequest = new HttpDelete(url.get().toString());
            break;
        case "HEAD":
            httpRequest = new HttpHead(url.get().toString());
            break;
        }
        String authorization = authorize(request.get().getRequestMethod(), date,
                request.get().getBucketName(), request.get().getObjName(),
                request.get().getUrl(), beCanonicalizedAMZHeaders(httpRequest));
        logger.debug(authorization);
        httpRequest.setHeader("Authorization", authorization);
        httpRequest.setHeader("Date", date);
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
        logger.debug(httpRequest.getURI().toString());
        for (Header head : httpRequest.getAllHeaders()) {
            logger.debug(head.getName() + ":" + head.getValue());
        }
        response.set(httpClient.execute(httpRequest, localContext));
        getResponse(response.get());
        return this;
    }

    private String beCanonicalizedAMZHeaders(HttpUriRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        for (String key : request.get().headers.keySet()) {
            if (key.startsWith("x-amz")) {
                sb.append(key + ":" + request.get().headers.get(key) + "\n");
            }
            logger.debug(key + ":" + request.get().headers.get(key) + "\n");
            httpRequest.setHeader(key, request.get().headers.get(key));
        }

        return sb.toString();
    }

    public static OOSClient getClient() {
        if (client == null) {
            synchronized (OOSClient.class) {
                if (client == null) {
                    client = new OOSClient();
                }
            }
        }
        return client;
    }

    private String authorize(String httpVerb, String date, String bucket,
            String objectName, String requestUrl,
            String CanonicalizedAMZHeaders)
            throws NoSuchAlgorithmException, IllegalStateException,
            UnsupportedEncodingException, InvalidKeyException {
        String stringToSign;
        String ContentMD5 = "";
        if (request.get().headers.containsKey("Content-MD5")) {
            ContentMD5 = request.get().headers.get("Content-MD5");
        }
        if (objectName.equalsIgnoreCase("")) {
            stringToSign = httpVerb + "\n" + ContentMD5 + "\n\n" + date + "\n"
                    + CanonicalizedAMZHeaders + "/" + bucket + requestUrl;
        } else {
            stringToSign = httpVerb + "\n" + ContentMD5 + "\n\n" + date + "\n"
                    + CanonicalizedAMZHeaders + "/" + bucket + "/" + objectName
                    + requestUrl;
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
        this.request.set(request);
        connect();
        return String.valueOf(response.get().getStatusLine().getStatusCode());
    }

    private String getResponse(HttpResponse response) throws IOException {
        responseCode.set(response.getStatusLine().getStatusCode());
        logger.debug(response.getStatusLine().toString());
        StringBuilder head = new StringBuilder();
        for (Header header : response.getAllHeaders()) {
            head.append(header.getName() + ":" + header.getValue() + "\n");
        }
        responseHeader.set(head.toString());
        logger.debug(head.toString());
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
        logger.debug(responseBody.get());
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
        request.get().setUrl(requestUrl);
    }
    
    @Scheduled(fixedRate = 60*1000)
    public void autosync() {
        if(poolingHttpClientConnectionManager != null) {
            logger.info("开始清除无效链接....");
            poolingHttpClientConnectionManager.closeExpiredConnections();
            poolingHttpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
   
        }
    }
    

}
