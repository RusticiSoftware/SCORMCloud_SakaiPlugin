/******************************************************************************
 * TestDataPreload.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.scormcloud.logic;

import java.lang.reflect.Field;
import java.util.Date;

import org.sakaiproject.scormcloud.model.ScormCloudItem;
import org.sakaiproject.genericdao.api.GenericDao;

/**
 * Contains test data for preloading and test constants
 * @author Sakai App Builder -AZ
 */
public class FakeDataPreload {

   /**
    * current user, access level user in LOCATION_ID1
    */
   public final static String USER_ID = "user-11111111";
   public final static String USER_DISPLAY = "Aaron Zeckoski";
   /**
    * access level user in LOCATION1_ID
    */
   public final static String ACCESS_USER_ID = "access-2222222";
   public final static String ACCESS_USER_DISPLAY = "Regular User";
   /**
    * maintain level user in LOCATION1_ID
    */
   public final static String MAINT_USER_ID = "maint-33333333";
   public final static String MAINT_USER_DISPLAY = "Maint User";
   /**
    * super admin user 
    */
   public final static String ADMIN_USER_ID = "admin";
   public final static String ADMIN_USER_DISPLAY = "Administrator";
   /**
    * Invalid user (also can be used to simulate the anonymous user) 
    */
   public final static String INVALID_USER_ID = "invalid-UUUUUU";

   /**
    * current location
    */
   public final static String LOCATION1_ID = "/site/ref-1111111";
   public final static String LOCATION1_TITLE = "Location 1 title";
   public final static String LOCATION2_ID = "/site/ref-22222222";
   public final static String LOCATION2_TITLE = "Location 2 title";
   public final static String INVALID_LOCATION_ID = "invalid-LLLLLLLL";

   // testing data objects here

   public ScormCloudItem item1 = new ScormCloudItem("Item 1", USER_ID, LOCATION1_ID, Boolean.FALSE, new Date());
   public ScormCloudItem item2 = new ScormCloudItem("Item 2", USER_ID, LOCATION2_ID, Boolean.FALSE, new Date());
   public ScormCloudItem accessitem = new ScormCloudItem("new access item", ACCESS_USER_ID, LOCATION1_ID, Boolean.FALSE, new Date());
   public ScormCloudItem maintitem = new ScormCloudItem("New maint item title", MAINT_USER_ID, LOCATION1_ID, Boolean.FALSE, new Date());
   public ScormCloudItem adminitem = new ScormCloudItem("New admin item title", ADMIN_USER_ID, LOCATION2_ID, Boolean.FALSE, new Date());

   public GenericDao dao;
   public void setDao(GenericDao dao) {
       this.dao = dao;
   }

   public void init() {
       preloadTestData();
   }

   /**
    * Preload a bunch of test data into the database
    */
   public void preloadTestData() {
      /*
       * This iterates over the fields in FakeDataPreload and finds all the ones which are
       * of the type ScormCloudItem, then it runs the dao save method on them:
       * dao.findById(ScormCloudItem.class, item1.getId());
       * This does the same thing as writing a bunch of these:
       * dao.save(item1);
       */
      Field[] fields = this.getClass().getDeclaredFields();
      for (Field field : fields) {
         if (field.getType().equals(ScormCloudItem.class)) {
            try {
               dao.save((ScormCloudItem)field.get(this));
            } catch (Exception e) {
               throw new RuntimeException(e.getMessage(), e);
            }
         }
      }
   }

   /**
    * Reload the test data back into the current session so they can be tested correctly,
    * if this is not done then the preloaded data is in a separate session and equality tests will not work
    */
   public void reloadTestData() {
      /*
       * This iterates over the fields in FakeDataPreload and finds all the ones which are
       * of the type ScormCloudItem, then it sets the field equal to the method:
       * dao.findById(ScormCloudItem.class, item1.getId());
       * This does the same thing as writing a bunch of these:
       * item1 = (ScormCloudItem) dao.findById(ScormCloudItem.class, item1.getId());
       */
      Field[] fields = this.getClass().getDeclaredFields();
      for (Field field : fields) {
         if (field.getType().equals(ScormCloudItem.class)) {
            try {
               field.set(this, dao.findById(ScormCloudItem.class, ((ScormCloudItem)field.get(this)).getId()));
            } catch (Exception e) {
               throw new RuntimeException(e.getMessage(), e);
            }
         }
      }
   }

}
