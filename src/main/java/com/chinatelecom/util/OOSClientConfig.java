package com.chinatelecom.util;

public class OOSClientConfig {
    
    public static final int CONN_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 30000;
    public static final int CONNECTION_REQUEST_TIMEOUT = 30000;
    public static final String ak = "f4f0e26e5952be5b04b7";
    public static final String sk = "21896471c739eff494895f2ec583b2039883e741";
    public static boolean isV4Signature = true;
    private String regionName = "hesjz";
    private String serviceName = "s3";
    private String host = "oos-"+regionName+".ctyunapi.cn";
    private String IamHost = "oos-"+regionName+"-iam.ctyunapi.cn";
    private int port = 80;
    private boolean isIAM = false;
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }
    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getRegionName() {
        return regionName;
    }
    
    public void setRegionName(String regionName) {
        this.regionName = regionName;
        if(isIAM) {
            IamHost = "oos-"+regionName+"-iam.ctyunapi.cn";
        }else {
            host = "oos-"+regionName+".ctyunapi.cn";
        }
    }
    
    public String getHost() {
        if(isIAM) {
            return IamHost;
        }else {
            return host;
        }
    }
    
    public void setIAMHost() {
        isIAM = true;
        serviceName = "sts";
    }
    
    public void setCommonHost() {
        isIAM = false;
        serviceName = "s3";
    }
    

}
