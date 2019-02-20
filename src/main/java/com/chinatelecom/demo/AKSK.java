package com.chinatelecom.demo;

import com.chinatelecom.request.AKSKRequest;
import com.chinatelecom.request.OOSClient;
import com.chinatelecom.util.AKSKAction;
import com.chinatelecom.util.KeyStatus;
import com.chinatelecom.util.Primary;

public class AKSK {
    
    public String createAKSK() throws Exception {
        OOSClient client = OOSClient.getIAMClient();
        AKSKAction action = AKSKAction.create;
        AKSKRequest request = new AKSKRequest(action);
        client.sendRequest(request);
        return String.valueOf(client.getResponseCode());
    }
    
    public String listAKSK() throws Exception {
        OOSClient client = OOSClient.getIAMClient();
        AKSKAction action = AKSKAction.list;
        AKSKRequest request = new AKSKRequest(action);
        client.sendRequest(request);
        return client.getResponseBody();
    }
    
    public String deleteAKSK(String ak) throws Exception {
        OOSClient client = OOSClient.getIAMClient();
        AKSKAction action = AKSKAction.delete;
        AKSKRequest request = new AKSKRequest(action);
        request.setAK(ak);
        client.sendRequest(request);
        return String.valueOf(client.getResponseCode());
    }
    
    public String updateAKSK(String ak, KeyStatus status, Primary primary) throws Exception {
        OOSClient client = OOSClient.getIAMClient();
        AKSKAction action = AKSKAction.update;
        AKSKRequest request = new AKSKRequest(action);
        request.setAK(ak);
        request.setPrimary(primary);
        request.setStatus(status);
        client.sendRequest(request);
        return String.valueOf(client.getResponseCode());
    }
    
}
