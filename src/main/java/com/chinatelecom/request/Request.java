package com.chinatelecom.request;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class Request {
    
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    private static final String DATE_STR = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(
            DATE_STR, Locale.ENGLISH);
    static {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        DATE_FMT.setTimeZone(gmt);
        TimeZone utc = TimeZone.getTimeZone("UTC");
        timeFormatter.setTimeZone(utc);
        dateFormatter.setTimeZone(utc);
    }
    
    private String requestUrl = "";
    private String requestMethod = "";
    private Map<String, String> headers = new HashMap<String, String>();
    private String bucketName = "";
    private String objName = "";
    private String requestBody = "";
    private URL url;
    private String serviceName = "s3";
    private String regionName;
    private String dateStamp;
    private String dateTimeStamp;
    private String date;
    
    protected Request() {
        Date now = new Date();
        setDateTimeStamp(timeFormatter.format(now));
        dateStamp = dateFormatter.format(now);
        date = DATE_FMT.format(now);
    }
    
    /**
     * @return the url
     */
    protected String getRequestUrl() {
        return requestUrl;
    }
    /**
     * @param url the url to set
     */
    protected Request setRequestUrl(String requestUrl) {
        if(this.requestUrl!=null&&!this.requestUrl.isEmpty()) {
            this.requestUrl += ("&"+requestUrl);
        }else {
            this.requestUrl = "?"+requestUrl;
        }
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
            this.requestBody += ("&"+requestBody);
        }else {
            this.requestBody = requestBody;
        }
    }
    /**
     * @return the url
     */
    protected URL getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    protected void setUrl(URL url) {
        this.url = url;
    }
    /**
     * @return the serviceName
     */
    protected String getServiceName() {
        return serviceName;
    }
    /**
     * @param serviceName the serviceName to set
     */
    protected void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    /**
     * @return the regionName
     */
    protected String getRegionName() {
        return regionName;
    }
    /**
     * @param regionName the regionName to set
     */
    protected void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    /**
     * @return the dateStamp
     */
    protected String getDateStamp() {
        return dateStamp;
    }
    /**
     * @param dateStamp the dateStamp to set
     */
    protected void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }
    /**
     * @return the date
     */
    protected String getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    protected void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the dateTimeStamp
     */
    protected String getDateTimeStamp() {
        return dateTimeStamp;
    }

    /**
     * @param dateTimeStamp the dateTimeStamp to set
     */
    protected void setDateTimeStamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }
    

}
