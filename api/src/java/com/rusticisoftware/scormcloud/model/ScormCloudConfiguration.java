/*
 *   Copyright 2009-2010 Rustici Software. Licensed under the
 *   Educational Community License, Version 2.0 (the "License"); you may
 *   not use this file except in compliance with the License. You may
 *   obtain a copy of the License at
 *   
 *   http://www.osedu.org/licenses/ECL-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */


package com.rusticisoftware.scormcloud.model;

public class ScormCloudConfiguration {
    public final String DEFAULT_SERVICE_URL = "http://cloud.scorm.com/EngineWebServices/";
    
    private String id;
    private Boolean isMasterConfig; //If true, is used when site config is missing...
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
        this.setContext(orig.getContext());
        this.setIsMasterConfig(orig.getIsMasterConfig());
        this.setAppId(orig.getAppId());
        this.setSecretKey(orig.getSecretKey());
        this.setServiceUrl(orig.getServiceUrl());
    }
    public Boolean getIsMasterConfig(){
        return isMasterConfig;
    }
    public void setIsMasterConfig(Boolean isMasterConfig){
        this.isMasterConfig = isMasterConfig;
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
