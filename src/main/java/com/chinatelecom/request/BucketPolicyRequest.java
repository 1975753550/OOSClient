package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketPolicyRequest extends Request{
    
    public BucketPolicyRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setRequestUrl("policy");
    }
    
    public BucketPolicyRequest setConfigXML(String configXML) {
        setRequestBody(configXML);
        return this;
    }

}
