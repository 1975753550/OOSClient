package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketWebsiteRequest extends Request {
    
    public BucketWebsiteRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setRequestUrl("website", "");
    }
    
    public BucketWebsiteRequest setConfigXML(String configXML) {
        setRequestBody(configXML);
        return this;
    }

}
