package com.chinatelecom.request;

import com.chinatelecom.util.AKSKAction;
import com.chinatelecom.util.Action;
import com.chinatelecom.util.KeyStatus;
import com.chinatelecom.util.Primary;

public class AKSKRequest extends Request {
    
    public AKSKRequest(AKSKAction action) {
        setRequestMethod(Action.POST.name());
//        setRequestUrl("Action", action.getName());
        setRequestBody("Action="+action.getName());
    }
    
    public AKSKRequest setAK(String ak) {
        
        setRequestUrl("AccessKeyId", ak);
        setRequestBody("AccessKeyId="+ak);
        return this;
    }
    
    public AKSKRequest setPrimary(Primary primary) {
        setRequestUrl("IsPrimary", String.valueOf(primary.getPrimary()));
        setRequestBody("IsPrimary="+primary.getPrimary());
        return this;
    }
    
    public AKSKRequest setStatus(KeyStatus status) {
        setRequestUrl("Status", status.name());
        setRequestBody("Status="+status.name());
        return this;
    }

}
