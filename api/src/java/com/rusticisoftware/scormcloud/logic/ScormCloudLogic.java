/******************************************************************************
 * ScormCloudLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package com.rusticisoftware.scormcloud.logic;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.rusticisoftware.scormcloud.model.ScormCloudConfiguration;
import com.rusticisoftware.scormcloud.model.ScormCloudPackage;
import com.rusticisoftware.scormcloud.model.ScormCloudRegistration;

/**
 * This is the interface for the app Logic, 
 * @author Sakai App Builder -AZ
 */
public interface ScormCloudLogic {
   
   public void saveScormCloudConfiguration(ScormCloudConfiguration config) throws Exception;
   public ScormCloudConfiguration getScormCloudConfiguration() throws Exception;
   
   public ScormCloudPackage getPackageById(String id);
   public List<ScormCloudPackage> getAllSitePackages();
   public void addNewPackage(ScormCloudPackage pkg, File packageZip) throws Exception;
   public void updatePackage(ScormCloudPackage pkg);
   public void removePackage(ScormCloudPackage pkg);
   public String getPackagePropertiesUrl(ScormCloudPackage pkg, String styleSheetUrl);
   public String getPackagePreviewUrl(ScormCloudPackage pkg, String redirectOnExitUrl);
   
   public ScormCloudRegistration getRegistrationById(String id);
   public List<ScormCloudRegistration> getRegistrationsByPackageId(String pkgId);
   public List<ScormCloudRegistration> getRegistrationsWherePropertiesEqual(Map<String, Object> propertyMap);
   public List<ScormCloudRegistration> getRegistrationsWherePropertiesLike(Map<String, Object> propertyMap);
   public ScormCloudRegistration findRegistrationFor(ScormCloudPackage pkg, String userId, String assignmentKey);
   public ScormCloudRegistration addNewRegistration(ScormCloudPackage pkg, String userId, String assignmentKey);
   public void updateRegistrationResultsFromCloud(ScormCloudRegistration reg);
   public void removeRegistration(ScormCloudRegistration reg);
   public void resetRegistration(ScormCloudRegistration reg);
   public String getLaunchUrl(ScormCloudRegistration reg, String redirectOnExitUrl);
   public String getLaunchHistoryReport(ScormCloudRegistration reg);
   public String getLaunchInfoXml(ScormCloudRegistration reg, String launchId);
   public Document getRegistrationReport(ScormCloudRegistration reg);
   
   public boolean isCurrentUserSakaiAdmin();
   public boolean isCurrentUserPluginAdmin();
   public boolean canConfigurePlugin();
   public boolean isPluginConfigured();
}
