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

import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudItem;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;

import com.rusticisoftware.hostedengine.client.ScormEngineService;

/**
 * This is the interface for the app Logic, 
 * @author Sakai App Builder -AZ
 */
public interface ScormCloudLogic {
	
   /**
    * This returns an item based on an id
    * @param id the id of the item to fetch
    * @return a ScormCloudItem or null if none found
    */
   public ScormCloudItem getItemById(Long id);

   /**
    * Check if a specified user can write this item in a specified site
    * @param item to be modified or removed
    * @param locationId a unique id which represents the current location of the user (entity reference)
    * @param userId the internal user id (not username)
    * @return true if item can be modified, false otherwise
    */
   public boolean canWriteItem(ScormCloudItem item, String locationId, String userId);

   /**
    * This returns a List of items for a specified site that are
    * visible to the specified user
    * @param locationId a unique id which represents the current location of the user (entity reference)
    * @param userId the internal user id (not username)
    * @return a List of ScormCloudItem objects
    */
   public List<ScormCloudItem> getAllVisibleItems(String locationId, String userId);

   /**
    * Save (Create or Update) an item (uses the current site)
    * @param item the ScormCloudItem to create or update
    */
   public void saveItem(ScormCloudItem item);

   /**
    * Remove an item
    * @param item the ScormCloudItem to remove
    */
   public void removeItem(ScormCloudItem item);
   
   /**
    * This returns a package based on an id
    * @param id the id of the package to fetch
    * @return a ScormCloudPackage or null if none found
    */
   public ScormCloudPackage getPackageById(String id);

   /**
    * Check if a specified user can write this item in a specified site
    * @param item to be modified or removed
    * @param locationId a unique id which represents the current location of the user (entity reference)
    * @param userId the internal user id (not username)
    * @return true if item can be modified, false otherwise
    */
   public boolean canWritePackage(ScormCloudPackage pkg, String locationId, String userId);

   /**
    * This returns a List of items for a specified site that are
    * visible to the specified user
    * @param locationId a unique id which represents the current location of the user (entity reference)
    * @param userId the internal user id (not username)
    * @return a List of ScormCloudItem objects
    */
   public List<ScormCloudPackage> getAllVisiblePackages(String locationId, String userId);

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
   public ScormCloudRegistration findRegistrationFor(String pkgId, String userId, String assignmentKey);
   public ScormCloudRegistration addNewRegistration(ScormCloudPackage pkg, String userId, String assignmentKey);
   
   public String getLaunchUrl(ScormCloudRegistration reg, String redirectOnExitUrl);
   
   public boolean canWriteRegistration(ScormCloudRegistration reg, String locationId, String userId);
   public void updateRegistration(ScormCloudRegistration reg);
   public void updateRegistrationResultsFromCloud(ScormCloudRegistration reg);
   public void removeRegistration(ScormCloudRegistration reg);
   public void resetRegistration(ScormCloudRegistration reg);
   
   public void setScormCloudConfiguration(ScormCloudConfiguration config);
   public ScormCloudConfiguration getScormCloudConfiguration();

}
