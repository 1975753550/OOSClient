package com.chinatelecom.demo;

import com.chinatelecom.request.*;
import com.chinatelecom.util.Action;
import com.chinatelecom.util.OOSClient;

public class Bucket {
    
    public String createBucket(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        BucketRequest request = new BucketRequest(bucketName, action);
        BucketRequest.ACL acl = BucketRequest.ACL.bucketPrivate;
        request.setACL(acl);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String deleteBucket(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        BucketRequest request = new BucketRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String deleteBucketCors(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        BucketCorsRequest request = new BucketCorsRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketCors(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketCorsRequest request = new BucketCorsRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String putBucketCors(String bucketName, String corsConfig) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        BucketCorsRequest request = new BucketCorsRequest(bucketName, action);
        request.setConfigXML(corsConfig);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String deleteBucketLifecycle(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        BucketLifecycleRequest request = new BucketLifecycleRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketLifecycle(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketLifecycleRequest request = new BucketLifecycleRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String putBucketLifecycle(String bucketName, String lifeConfig) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        BucketLifecycleRequest request = new BucketLifecycleRequest(bucketName, action);
        request.setConfigXML(lifeConfig);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String deleteBucketPolicy(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        BucketPolicyRequest request = new BucketPolicyRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketPolicy(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketPolicyRequest request = new BucketPolicyRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String putBucketPolicy(String bucketName, String policyConfig) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        BucketPolicyRequest request = new BucketPolicyRequest(bucketName, action);
        request.setConfigXML(policyConfig);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String DeleteBucketWebsit(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        BucketWebsiteRequest request = new BucketWebsiteRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketWebsit(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketWebsiteRequest request = new BucketWebsiteRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String putBucketWebsit(String bucketName, String websitConfig) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        BucketWebsiteRequest request = new BucketWebsiteRequest(bucketName, action);
        request.setConfigXML(websitConfig);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketLogging(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketLoggingRequest request = new BucketLoggingRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String putBucketLogging(String bucketName, String loggingConfig) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketLoggingRequest request = new BucketLoggingRequest(bucketName, action);
        request.setConfigXML(loggingConfig);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucketACL(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketACLRequest request = new BucketACLRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String headBucket(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.HEAD;
        BucketRequest request = new BucketRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String getBucket(String bucketName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        BucketRequest request = new BucketRequest(bucketName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }

}
