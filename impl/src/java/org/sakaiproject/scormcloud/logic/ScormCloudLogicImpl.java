/******************************************************************************
 * ScormCloudLogicImpl.java - created by Sakai App Builder -AZ
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.api.search.Search;

import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.dao.ScormCloudDao;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
import org.sakaiproject.scormcloud.model.ScormCloudItem;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;

import com.rusticisoftware.hostedengine.client.ScormEngineService;

/**
 * This is the implementation of the business logic interface
 * @author Sakai App Builder -AZ
 */
public class ScormCloudLogicImpl implements ScormCloudLogic {

   private static Log log = LogFactory.getLog(ScormCloudLogicImpl.class);

   private ScormCloudDao dao;
   public void setDao(ScormCloudDao dao) {
      this.dao = dao;
   }

   private ExternalLogic externalLogic;
   public void setExternalLogic(ExternalLogic externalLogic) {
      this.externalLogic = externalLogic;
   }
   
   private ScormEngineService scormEngineService;
   public void setScormEngineService(ScormEngineService service){
	   this.scormEngineService = service;
   }


   /**
    * Place any code that should run when this class is initialized by spring here
    */
   public void init() {
      log.debug("init");
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.scormcloud.logic.ScormCloudLogic#getItemById(java.lang.Long)
    */
   public ScormCloudItem getItemById(Long id) {
      log.debug("Getting item by id: " + id);
      return dao.findById(ScormCloudItem.class, id);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.scormcloud.logic.ScormCloudLogic#canWriteItem(org.sakaiproject.scormcloud.model.ScormCloudItem, java.lang.String, java.lang.String)
    */
   public boolean canWriteItem(ScormCloudItem item, String locationId, String userId) {
      log.debug("checking if can write for: " + userId + ", " + locationId + ": and item=" + item.getTitle() );
      if (item.getOwnerId().equals( userId ) ) {
         // owner can always modify an item
         return true;
      } else if ( externalLogic.isUserAdmin(userId) ) {
         // the system super user can modify any item
         return true;
      } else if ( locationId.equals(item.getLocationId()) &&
            externalLogic.isUserAllowedInLocation(userId, ExternalLogic.ITEM_WRITE_ANY, locationId) ) {
         // users with permission in the specified site can modify items from that site
         return true;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.scormcloud.logic.ScormCloudLogic#getAllVisibleItems(java.lang.String, java.lang.String)
    */
   public List<ScormCloudItem> getAllVisibleItems(String locationId, String userId) {
      log.debug("Fetching visible items for " + userId + " in site: " + locationId);
      List<ScormCloudItem> l = null;
      if (locationId == null) {
         // get all items
         l = dao.findAll(ScormCloudItem.class);
      } else {
         l = dao.findBySearch(ScormCloudItem.class, 
               new Search("locationId", locationId) );
      }
      // check if the current user can see all items (or is super user)
      if ( externalLogic.isUserAdmin(userId) || 
            externalLogic.isUserAllowedInLocation(userId, ExternalLogic.ITEM_READ_HIDDEN, locationId) ) {
         log.debug("Security override: " + userId + " able to view all items");
      } else {
         // go backwards through the loop to avoid hitting the "end" early
         for (int i=l.size()-1; i >= 0; i--) {
            ScormCloudItem item = (ScormCloudItem) l.get(i);
            if ( item.getHidden().booleanValue() &&
                  !item.getOwnerId().equals(userId) ) {
               l.remove(item);
            }
         }
      }
      return l;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.scormcloud.logic.ScormCloudLogic#removeItem(org.sakaiproject.scormcloud.model.ScormCloudItem)
    */
   public void removeItem(ScormCloudItem item) {
      log.debug("In removeItem with item:" + item.getId() + ":" + item.getTitle());
      // check if current user can remove this item
      if ( canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId() ) ) {
         dao.delete(item);
         log.info("Removing item: " + item.getId() + ":" + item.getTitle());
      } else {
         throw new SecurityException("Current user cannot remove item " + 
               item.getId() + " because they do not have permission");
      }
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.scormcloud.logic.ScormCloudLogic#saveItem(org.sakaiproject.scormcloud.model.ScormCloudItem)
    */
   public void saveItem(ScormCloudItem item) {
      log.debug("In saveItem with item:" + item.getTitle());
      // set the owner and site to current if they are not set
      if (item.getOwnerId() == null) {
         item.setOwnerId( externalLogic.getCurrentUserId() );
      }
      if (item.getLocationId() == null) {
         item.setLocationId( externalLogic.getCurrentLocationId() );
      }
      if (item.getDateCreated() == null) {
         item.setDateCreated( new Date() );
      }
      // save item if new OR check if the current user can update the existing item
      if ( (item.getId() == null) || 
            canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
         dao.save(item);
         log.info("Saving item: " + item.getId() + ":" + item.getTitle());
      } else {
         throw new SecurityException("Current user cannot update item " + 
               item.getId() + " because they do not have permission");
      }
   }
   
   
   public ScormCloudPackage getPackageById(String id) {
	   log.debug("Getting package by id: " + id);
	   return dao.findById(ScormCloudPackage.class, id);
   }
   
   public boolean canWritePackage(ScormCloudPackage pkg, String locationId, String userId) {
      log.debug("checking if can write for: " + userId + ", " + locationId + ": and pkg title=" + pkg.getTitle() );
      if (pkg.getOwnerId().equals( userId ) ) {
         // owner can always modify an item
         return true;
      } else if ( externalLogic.isUserAdmin(userId) ) {
         // the system super user can modify any item
         return true;
      } else if ( locationId.equals(pkg.getLocationId()) &&
            externalLogic.isUserAllowedInLocation(userId, ExternalLogic.ITEM_WRITE_ANY, locationId) ) {
         // users with permission in the specified site can modify items from that site
         return true;
      }
      return false;
   }
   
   public List<ScormCloudPackage> getAllVisiblePackages(String locationId, String userId) {
      log.debug("Fetching visible items for " + userId + " in site: " + locationId);
      List<ScormCloudPackage> l = null;
      if (locationId == null) {
         // get all items
         l = dao.findAll(ScormCloudPackage.class);
      } else {
         l = dao.findBySearch(ScormCloudPackage.class, 
               new Search("locationId", locationId) );
      }
      // check if the current user can see all items (or is super user)
      if ( externalLogic.isUserAdmin(userId) || 
            externalLogic.isUserAllowedInLocation(userId, ExternalLogic.ITEM_READ_HIDDEN, locationId) ) {
         log.debug("Security override: " + userId + " able to view all items");
      } else {
         // go backwards through the loop to avoid hitting the "end" early
         for (int i=l.size()-1; i >= 0; i--) {
            ScormCloudPackage pkg = (ScormCloudPackage) l.get(i);
            if ( pkg.getHidden().booleanValue() &&
                  !pkg.getOwnerId().equals(userId) ) {
               l.remove(pkg);
            }
         }
      }
      return l;
   }
   
   public void removePackage(ScormCloudPackage pkg) {
      log.debug("In removePackage with item:" + pkg.getId() + ":" + pkg.getTitle());
      // check if current user can remove this item
      if ( canWritePackage(pkg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId() ) ) {
         dao.delete(pkg);
         log.info("Removing item: " + pkg.getId() + ":" + pkg.getTitle());
      } else {
         throw new SecurityException("Current user cannot remove item " + 
               pkg.getId() + " because they do not have permission");
      }
   }
   
   public void addNewPackage(ScormCloudPackage pkg, File packageZip) throws Exception
   {
	   log.debug("In addNewPackage with package:" + pkg.getTitle());
	      // set the owner and site to current if they are not set
	      if (pkg.getOwnerId() == null) {
	         pkg.setOwnerId( externalLogic.getCurrentUserId() );
	      }
	      if (pkg.getLocationId() == null) {
	         pkg.setLocationId( externalLogic.getCurrentLocationId() );
	      }
	      if (pkg.getDateCreated() == null) {
	         pkg.setDateCreated( new Date() );
	      }
	      // save pkg if new OR check if the current user can update the existing pkg
	      if (pkg.getId() == null) {
	    	  scormEngineService.getCourseService().ImportCourse(pkg.getScormCloudId(), packageZip.getAbsolutePath());
	  		 
	         dao.save(pkg);
	         log.info("Saving package: " + pkg.getId() + ":" + pkg.getTitle());
	      } else {
	         throw new SecurityException("Current user cannot update package " + 
	               pkg.getId() + " because they do not have permission");
	      }
   }
   
   
   public void updatePackage(ScormCloudPackage pkg) {
      log.debug("In saveItem with item:" + pkg.getTitle());
      // set the owner and site to current if they are not set
      if (pkg.getOwnerId() == null) {
         pkg.setOwnerId( externalLogic.getCurrentUserId() );
      }
      if (pkg.getLocationId() == null) {
         pkg.setLocationId( externalLogic.getCurrentLocationId() );
      }
      if (pkg.getDateCreated() == null) {
         pkg.setDateCreated( new Date() );
      }
      // save pkg if new OR check if the current user can update the existing pkg
      if ( canWritePackage(pkg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
         dao.save(pkg);
         log.info("Saving package: " + pkg.getId() + ":" + pkg.getTitle());
      } else {
         throw new SecurityException("Current user cannot update package " + 
               pkg.getId() + " because they do not have permission");
      }
   }
   
   
   public String getLaunchUrl(ScormCloudPackage pkg) {
	   
	   //TODO: implement a does reg exist type functionality on the cloud
	   String currentUserId = externalLogic.getCurrentUserId();
	   String userDisplayName = externalLogic.getUserDisplayName(currentUserId);
	   String firstName = "sakai";
	   String lastName = "learner";
	   
	   if (userDisplayName != null && userDisplayName.contains(" ")){
		   String[] nameParts = userDisplayName.split(" ");
		   firstName = nameParts[0];
		   lastName = nameParts[1];
	   }
		   
	   try {
		   scormEngineService.getRegistrationService().CreateRegistration(currentUserId, pkg.getScormCloudId(), currentUserId, firstName, lastName);
	   } catch (Exception e) {
		   log.debug("exception thrown creating reg (probably already exists)", e);
	   }
	   try {
		   return scormEngineService.getRegistrationService().GetLaunchUrl(currentUserId);
	   } catch (Exception e){
		   return "error.jsp";
	   }
   }

   
    public ScormCloudRegistration addNewRegistration(String userId, ScormCloudPackage pkg){
    	//TODO: implement a does reg exist type functionality on the cloud
 	   String userDisplayName = externalLogic.getUserDisplayName(userId);
 	   String firstName = "sakai";
 	   String lastName = "learner";
 	   
 	   if (userDisplayName != null && userDisplayName.contains(" ")){
 		   String[] nameParts = userDisplayName.split(" ");
 		   firstName = nameParts[0];
 		   lastName = nameParts[1];
 	   }
 		   
 	   try {
 		   String cloudRegId = "sakai-reg-" + userId + "-" + UUID.randomUUID().toString();
 		   scormEngineService.getRegistrationService().CreateRegistration(cloudRegId, pkg.getScormCloudId(), userId, firstName, lastName);
 		   ScormCloudRegistration reg = new ScormCloudRegistration();
 		   reg.setDateCreated(new Date());
 		   reg.setLocationId(externalLogic.getCurrentLocationId());
 		   reg.setOwnerId(externalLogic.getCurrentUserId());
 		   reg.setScormCloudId(cloudRegId);
 		   dao.save(reg);
 		   return reg;
 	   } catch (Exception e) {
 		   log.debug("exception thrown creating reg", e);
 		   return null;
 	   }
    }
   
	public ScormCloudRegistration getRegistrationForUser(String userId) {
		   log.debug("Getting registration for user with id: " + userId);
		   return dao.findById(ScormCloudRegistration.class, userId);
	}
	
	 public boolean canWriteRegistration(ScormCloudRegistration reg, String locationId, String userId) {
	      log.debug("checking if can write for: " + userId + ", " + locationId);
	      if (reg.getOwnerId().equals( userId ) ) {
	         // owner can always modify an item
	         return true;
	      } else if ( externalLogic.isUserAdmin(userId) ) {
	         // the system super user can modify any item
	         return true;
	      } else if ( locationId.equals(reg.getLocationId()) &&
	            externalLogic.isUserAllowedInLocation(userId, ExternalLogic.ITEM_WRITE_ANY, locationId) ) {
	         // users with permission in the specified site can modify items from that site
	         return true;
	      }
	      return false;
	   }
	
	public void removeRegistration(ScormCloudRegistration reg) {
		log.debug("In regmoveRegistration with regId:" + reg.getId());
	      // check if current user can remove this item
	      if ( canWriteRegistration(reg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId() ) ) {
	         dao.delete(reg);
	         log.info("Removing reg with id: " + reg.getId());
	      } else {
	         throw new SecurityException("Current user cannot remove item " + 
	               reg.getId() + " because they do not have permission");
	      }
	}
	
	
	public void updateRegistration(ScormCloudRegistration reg) {
		log.debug("In updateRegistration with reg id:" + reg.getId());
	      // set the owner and site to current if they are not set
	      if (reg.getOwnerId() == null) {
	         reg.setOwnerId( externalLogic.getCurrentUserId() );
	      }
	      if (reg.getLocationId() == null) {
	         reg.setLocationId( externalLogic.getCurrentLocationId() );
	      }
	      if (reg.getDateCreated() == null) {
	         reg.setDateCreated( new Date() );
	      }
	      // save pkg if new OR check if the current user can update the existing pkg
	      if ( canWriteRegistration(reg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
	         dao.save(reg);
	         log.info("Saving registration: " + reg.getId());
	      } else {
	         throw new SecurityException("Current user cannot update package " + 
	               reg.getId() + " because they do not have permission");
	      }
	}

}
