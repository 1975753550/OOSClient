package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketLoggingRequest extends Request {
    
    public BucketLoggingRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setRequestUrl("logging", "");
    }
    
    public BucketLoggingRequest setConfigXML(String configXML) {
        setRequestBody(configXML);
        return this;
    }

}
