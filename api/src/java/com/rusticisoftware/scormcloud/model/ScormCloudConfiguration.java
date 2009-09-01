package com.rusticisoftware.scormcloud.model;

public class ScormCloudConfiguration {
    public final String DEFAULT_SERVICE_URL = "http://cloud.scorm.com/EngineWebServices/";
    
    private String id;
    private String context;
    private String appId;
    private String secretKey;
    private String serviceUrl = DEFAULT_SERVICE_URL;
    
    public ScormCloudConfiguration(){
    }
    
    public ScormCloudConfiguration(ScormCloudConfiguration orig){
        this.copyFrom(orig);
    }
    
    public void copyFrom(ScormCloudConfiguration orig){
        this.setAppId(orig.getAppId());
        this.setSecretKey(orig.getSecretKey());
        this.setServiceUrl(orig.getServiceUrl());
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContext(){
        return context;
    }
    public void setContext(String context){
        this.context = context;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getServiceUrl() {
        return serviceUrl;
    }
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
}
