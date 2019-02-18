package com.chinatelecom.request;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    
    Logger logger = LoggerFactory.getLogger(Response.class);
    
    private int responseCode;
    private String responseHeader = "";
    private String responseBody = "";
    
    Response(HttpResponse response) throws IOException{
        getResponse(response);
    }
    
    private Response getResponse(HttpResponse response) throws IOException {
        responseCode = response.getStatusLine().getStatusCode();
        if (logger.isDebugEnabled()) {
            logger.debug(response.getStatusLine().toString());
        }
        StringBuilder head = new StringBuilder();
        for (Header header : response.getAllHeaders()) {
            head.append(header.getName() + ":" + header.getValue() + "\n");
        }
        responseHeader = head.toString();
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
        responseBody = sb.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(responseBody);
        }
        return this;
    }

    int getResponseCode() {
        return responseCode;
    }

    String getResponseHeader() {
        return responseHeader;
    }

    String getResponseBody() {
        return responseBody;
    }

}
