package com.chinatelecom.request;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
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
import com.chinatelecom.util.OOSClientConfig;
import com.chinatelecom.request.OOSV4Sign;
import static com.chinatelecom.util.OOSClientConfig.*;

@Component
public class OOSClient {

    private static Logger logger = LoggerFactory.getLogger(OOSClient.class);

    private ThreadLocal<Request> request = new ThreadLocal<Request>();
    private ThreadLocal<Response> response = new ThreadLocal<Response>();
    private ThreadLocal<HttpResponse> httpResponse = new ThreadLocal<HttpResponse>();
    private volatile static OOSClient client;
    private volatile static OOSClient IAMclient;
    private final HttpClient httpClient;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private OOSClientConfig config;

    private OOSClient(OOSClientConfig config) {
        this.config = config;

        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(200);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(200);
        HttpHost httpHost = new HttpHost(config.getHost(), config.getPort());
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
    
    public static OOSClient getClient() {
        if (client == null) {
            synchronized (OOSClient.class) {
                if (client == null) {
                    OOSClientConfig config = new OOSClientConfig();
                    client = new OOSClient(config);
                }
            }
        }
        return client;
    }

    public static OOSClient getIAMClient() {
        if (IAMclient == null) {
            synchronized (OOSClient.class) {
                if (IAMclient == null) {
                    OOSClientConfig config = new OOSClientConfig();
                    config.setIAMHost();
                    IAMclient = new OOSClient(config);
                }
            }
        }
        return IAMclient;
    }

    public OOSClient connect() throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException {
        if (request.get().getObjName() != null
                && request.get().getObjName().equalsIgnoreCase("")) {
            request.get().setUrl(new URL("http", config.getHost(),
                    config.getPort(), "/" + request.get().getRequestUrl()));
        } else {
            request.get()
                    .setUrl(new URL("http", config.getHost(), config.getPort(),
                            "/" + request.get().getObjName()
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
        if (isV4Signature) {
            authorization = OOSV4Sign.authorize(request.get(), httpRequest,
                    config);
        } else {
            authorization = OOSV2Sign.authorize(request.get(), 
                    beCanonicalizedAMZHeaders(httpRequest), httpRequest);
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

        httpResponse.set(httpClient.execute(httpRequest));
        response.get().getResponse(httpResponse.get());
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
            if (key.equals("content-length"))
                continue;
            httpRequest.setHeader(key, request.get().getHeaders().get(key));
        }

        return sb.toString();
    }

    public String sendRequest(Request request) throws Exception {
        this.request.set(request);
        request.putHeader("host",
                request.getBucketName() + "." + config.getHost());
        request.setRegionName(config.getRegionName());
        request.setServiceName(config.getServiceName());
        request.putHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
        // request.putHeader("Connection", "Keep-Alive");
        request.putHeader("User-Agent", "Zw_Acoll");
        request.putHeader("Accept-Encoding", "gzip,deflate");
        connect();
        return String
                .valueOf(httpResponse.get().getStatusLine().getStatusCode());
    }

    public int getResponseCode() {
        return response.get().getResponseCode();
    }

    public String getResponseHeader() {
        return response.get().getResponseHeader();
    }

    public String getResponseBody() {
        return response.get().getResponseBody();
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
