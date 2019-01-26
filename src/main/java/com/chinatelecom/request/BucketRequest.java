package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class BucketRequest extends Request{
    
    public BucketRequest(String bucketName, Action action) {
        setBucketName(bucketName);
        setRequestMethod(action.name());
    }
    
    public BucketRequest setACL(ACL acl) {
        switch(acl) {
        case bucketPrivate:
            putHeader("x-amz-acl", "private");
        case bucketPublic:
            putHeader("x-amz-acl", "public-read-write");
        case bucketReadOnly:
            putHeader("x-amz-acl", "public-read");
        }
        return this;
    }
    
    
    public static enum ACL {
        bucketPrivate, bucketPublic, bucketReadOnly
    }

}
