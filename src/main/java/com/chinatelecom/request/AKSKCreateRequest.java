package com.chinatelecom.request;

import com.chinatelecom.util.Action;

public class AKSKCreateRequest extends Request {
    
    public AKSKCreateRequest(Action action) {
        setRequestMethod(action.name());
        setUrl("?Action=CreateAccessKey");
        setRequestBody("Action=CreateAccessKey");
    }

}
