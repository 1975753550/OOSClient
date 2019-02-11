package com.chinatelecom.demo;

import com.chinatelecom.request.AKSKCreateRequest;
import com.chinatelecom.request.OOSClient;
import com.chinatelecom.util.Action;

public class AKSK {
    
    public String createAKSK() throws Exception {
        OOSClient client = OOSClient.getClient();
        Action action = Action.POST;
        AKSKCreateRequest request = new AKSKCreateRequest(action);
        client.sendRequest(request);
        return String.valueOf(client.getResponseCode());
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(new AKSK().createAKSK());
    }

}
