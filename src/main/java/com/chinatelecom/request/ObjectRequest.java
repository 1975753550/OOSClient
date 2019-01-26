package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class ObjectRequest extends Request {
    
    public ObjectRequest(String bucketName, String objectName, Action action) {
        setBucketName(bucketName);
        setObjName(objectName);
        setRequestMethod(action.name());
    }
    
    public ObjectRequest setdata(String data) {
        setRequestBody(data);
        return this;
    }
    
    public ObjectRequest setSource(String sourceBucketName, String sourceObjectName) {
        putHeader("x-amz-copy-source", "/"+sourceBucketName+"/"+sourceObjectName);
        return this;
    }

}
