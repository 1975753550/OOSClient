package com.chinatelecom.request;

import java.util.HashMap;
import java.util.Map;

public abstract class Request {
    
    protected String requestUrl = "";
    protected String requestMethod = "";
    protected Map<String, String> headers = new HashMap<String, String>();
    protected String bucketName = "";
    protected String objName = "";
    protected String requestBody = "";
    
    /**
     * @return the url
     */
    protected String getUrl() {
        return requestUrl;
    }
    /**
     * @param url the url to set
     */
    protected Request setUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }
    /**
     * @return the requestMethod
     */
    protected String getRequestMethod() {
        return requestMethod;
    }
    /**
     * @param requestMethod the requestMethod to set
     */
    protected Request setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }
    /**
     * @return the bucketName
     */
    protected String getBucketName() {
        return bucketName;
    }
    /**
     * @param bucketName the bucketName to set
     */
    protected Request setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
    /**
     * @return the objName
     */
    protected String getObjName() {
        return objName;
    }
    /**
     * @param objName the objName to set
     */
    protected Request setObjName(String objName) {
        this.objName = objName;
        return this;
    }
    /**
     * @return the headers
     */
    protected Map<String, String> getHeaders() {
        return headers;
    }
    /**
     * @param headers the headers to set
     */
    protected void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    protected void putHeader(String key, String value) {
        if(headers.containsKey(key)) {
            String oldValue = headers.get(key);
            headers.put(key, oldValue+","+value);
        }
        headers.put(key, value);
    }
    /**
     * @return the requestBody
     */
    protected String getRequestBody() {
        return requestBody;
    }
    /**
     * @param requestBody the requestBody to set
     */
    protected void setRequestBody(String requestBody) {
        if(this.requestBody!=null&&!this.requestBody.isEmpty()) {
            this.requestBody += requestBody;
        }else {
            this.requestBody = requestBody;
        }
    }
    

}
