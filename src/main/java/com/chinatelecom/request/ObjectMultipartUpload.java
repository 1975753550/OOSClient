package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class ObjectMultipartUpload extends Request {
    
    public ObjectMultipartUpload(String bucketName, String objectName, Action action) {
        setBucketName(bucketName);
        setObjName(objectName);
        setRequestMethod(action.name());
    }
    
    public ObjectMultipartUpload setUploadId(String uploadId) {
        setRequestBody("uploadId="+uploadId);
        return this;
    }
    
    public ObjectMultipartUpload setPartNumber(String partNumber) {
        setRequestBody("partNumber="+partNumber);
        return this;
    }
    
    public ObjectMultipartUpload setContentLenth(String contentLenth) {
        putHeader("Content-Length", contentLenth);
        return this;
    }
    
    public ObjectMultipartUpload setData(String data) {
        setRequestBody(data);
        return this;
    }

}
