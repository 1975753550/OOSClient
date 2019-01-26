package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketLifecycleRequest extends Request {
    
    public BucketLifecycleRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setUrl("?lifecycle");
    }
    
    public BucketLifecycleRequest setConfigXML(String configXML) {
        setRequestBody(configXML);
        return this;
    }
    
    public BucketLifecycleRequest setContentMD5(String md5) {
        putHeader("Content-MD5", md5);
        return this;
    }

}
