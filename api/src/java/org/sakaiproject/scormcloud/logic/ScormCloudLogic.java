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

package org.sakaiproject.scormcloud.logic;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is the interface for the app Logic, 
 * @author Sakai App Builder -AZ
 */
public interface ScormCloudLogic {
   
   public void setScormCloudConfiguration(ScormCloudConfiguration config);
   public ScormCloudConfiguration getScormCloudConfiguration();
    
   /**
    * This returns a package based on an id
    * @param id the id of the package to fetch
    * @return a ScormCloudPackage or null if none found
    */
   public ScormCloudPackage getPackageById(String id);

   public List<ScormCloudPackage> getAllSitePackages();

   public void addNewPackage(ScormCloudPackage pkg, File packageZip) throws Exception;
   
   /**
    * Save (Create or Update) an item (uses the current site)
    * @param item the ScormCloudItem to create or update
    */
   public void updatePackage(ScormCloudPackage pkg);

   /**
    * Remove an item
    * @param item the ScormCloudItem to remove
    */
   public void removePackage(ScormCloudPackage pkg);
   
   public String getPackagePropertiesUrl(ScormCloudPackage pkg);
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
   public Document getRegistrationReport(ScormCloudRegistration reg);
   
   public String getAssignmentNameFromId(String id);
   
   public boolean isCurrentUserSakaiAdmin();
   public boolean isCurrentUserPluginAdmin();
   public boolean canConfigurePlugin();
   public boolean isPluginConfigured();
}
