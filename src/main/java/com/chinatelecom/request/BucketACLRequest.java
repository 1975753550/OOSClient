package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketACLRequest extends Request {
    
    public BucketACLRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
        setUrl("?acl");
    }

}
