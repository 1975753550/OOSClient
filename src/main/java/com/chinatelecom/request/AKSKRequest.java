package com.chinatelecom.request;

import com.chinatelecom.util.AKSKAction;
import com.chinatelecom.util.Action;
import com.chinatelecom.util.KeyStatus;
import com.chinatelecom.util.Primary;

public class AKSKRequest extends Request {
    
    public AKSKRequest(AKSKAction action) {
        setRequestMethod(Action.POST.name());
        setUrl("?Action="+action.getName());
        setRequestBody("Action="+action.getName());
        putHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    }
    
    public AKSKRequest setAK(String ak) {
        
        setUrl("&AccessKeyId="+ak);
        setRequestBody("&AccessKeyId="+ak);
        return this;
    }
    
    public AKSKRequest setPrimary(Primary primary) {
        setUrl("&IsPrimary="+primary.getPrimary());
        setRequestBody("&IsPrimary="+primary.getPrimary());
        return this;
    }
    
    public AKSKRequest setStatus(KeyStatus status) {
        setUrl("&Status="+status.name());
        setRequestBody("&Status="+status.name());
        return this;
    }

}
