package com.chinatelecom.demo;

import com.chinatelecom.request.*;
import com.chinatelecom.util.Action;
import com.chinatelecom.util.OOSClient;

public class Object {
    
    
    public String getObject(String bucketName, String objName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        ObjectRequest request = new ObjectRequest(bucketName, objName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String deleteObject(String bucketName, String objName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        ObjectRequest request = new ObjectRequest(bucketName, objName, action);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String putObject(String bucketName, String objName, String data) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        ObjectRequest request = new ObjectRequest(bucketName, objName, action);
        request.setdata(data);
        client.sendRequest(request);
        client.connect();
        return client.getResponseCode()+"";
    }
    
    public String InitialMultipartUpload(String bucketName, String objectName) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.POST;
        ObjectMultipartUpload request = new ObjectMultipartUpload(bucketName, objectName, action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String abortMultipartUploadObject(String bucketName, String objectName, String uploadId) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.DELETE;
        ObjectMultipartUpload request = new ObjectMultipartUpload(bucketName, objectName, action);
        request.setUploadId(uploadId);
        client.sendRequest(request);
        return client.getResponseCode()+"";
    }
    
    public String completeMultipartUploadObject(String bucketName, String objectName, String uploadId, 
            String Etag, String partNumber) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.POST;
        ObjectMultipartUpload request = new ObjectMultipartUpload(bucketName, objectName, action);
        request.setUploadId(uploadId);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String listObjectPart(String bucketName, String objectName, String uploadId) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.GET;
        ObjectMultipartUpload request = new ObjectMultipartUpload(bucketName, objectName, action);
        request.setUploadId(uploadId);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String uploadPartObject(String bucketName, String objectName, String uploadId,
            String partNumber, String date) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        ObjectMultipartUpload request = new ObjectMultipartUpload(bucketName, objectName, action);
        request.setUploadId(uploadId);
        request.setPartNumber(partNumber);
        request.setContentLenth(date.length()+"");
        request.setData(date);
        client.sendRequest(request);
        return client.getResponseHeader();
    }
    
    public String copyObject(String sourceBucket, String sourceObject, String destinationBucket, String destinationObject) throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.PUT;
        ObjectRequest request = new ObjectRequest(destinationBucket, destinationObject, action);
        request.setSource(sourceBucket, sourceObject);
        client.sendRequest(request);
        return client.getResponseBody();
        
    }
    

}
