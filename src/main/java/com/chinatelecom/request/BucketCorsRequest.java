package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketCorsRequest extends Request {
    
    
    public BucketCorsRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setRequestUrl("cors", "");
    }
    
    public BucketCorsRequest setConfigXML(String configXML) {
        setRequestBody(configXML);
        return this;
    }

}
