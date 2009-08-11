/******************************************************************************
 * ScormCloudLogicImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.scormcloud.logic;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.sakaiproject.scormcloud.dao.ScormCloudDao;
import org.sakaiproject.scormcloud.logic.ScormCloudLogicImpl;
import org.sakaiproject.scormcloud.logic.stubs.ExternalLogicStub;
import org.sakaiproject.scormcloud.model.ScormCloudItem;
import org.springframework.test.AbstractTransactionalSpringContextTests;


/**
 * Testing the Logic implementation methods
 * @author Sakai App Builder -AZ
 */
public class ScormCloudLogicImplTest extends AbstractTransactionalSpringContextTests {

   protected ScormCloudLogicImpl logicImpl;

   private FakeDataPreload tdp;

   protected String[] getConfigLocations() {
      // point to the needed spring config files, must be on the classpath
      // (add component/src/webapp/WEB-INF to the build path in Eclipse),
      // they also need to be referenced in the project.xml file
      return new String[] {"hibernate-test.xml", "spring-hibernate.xml"};
   }

   // run this before each test starts
   protected void onSetUpBeforeTransaction() throws Exception {
   }

   // run this before each test starts and as part of the transaction
   protected void onSetUpInTransaction() {
      // load the spring created dao class bean from the Spring Application Context
      ScormCloudDao dao = (ScormCloudDao) applicationContext.
         getBean("org.sakaiproject.scormcloud.dao.ScormCloudDao");
      if (dao == null) {
         throw new NullPointerException("DAO could not be retrieved from spring context");
      }

      // load up the test data preloader from spring
      tdp = (FakeDataPreload) applicationContext.
         getBean("org.sakaiproject.scormcloud.logic.test.FakeDataPreload");
      if (tdp == null) {
         throw new NullPointerException("FakeDataPreload could not be retrieved from spring context");
      }

      // reload the test objects in this session
      tdp.reloadTestData();
      
      // init the class if needed

      // setup the mock objects

      // create and setup the object to be tested
      logicImpl = new ScormCloudLogicImpl();
      logicImpl.setDao(dao);
      logicImpl.setExternalLogic( new ExternalLogicStub() );

      // can set up the default mock object returns here if desired
      // Note: Still need to activate them in the test methods though

      // run the init
      logicImpl.init();
   }

   /**
    * ScormCloud method for {@link org.sakaiproject.scormcloud.logic.impl.ScormCloudLogicImpl#getItemById(java.lang.Long)}.
    */
   public void testGetItemById() {
      ScormCloudItem item = logicImpl.getItemById(tdp.item1.getId());
      Assert.assertNotNull(item);
      Assert.assertEquals(item, tdp.item1);

      ScormCloudItem baditem = logicImpl.getItemById( new Long(-1) );
      Assert.assertNull(baditem);

      try {
         logicImpl.getItemById(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } catch (IllegalArgumentException e) {
         Assert.assertNotNull(e.getMessage());
      }
   }

   /**
    * ScormCloud method for {@link org.sakaiproject.scormcloud.logic.impl.ScormCloudLogicImpl#canWriteItem(org.sakaiproject.scormcloud.model.ScormCloudItem, java.lang.String, java.lang.String)}.
    */
   public void testCanWriteItemScormCloudItemStringString() {
      // testing perms as a normal user
      Assert.assertFalse( logicImpl.canWriteItem(tdp.adminitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.USER_ID) );
      Assert.assertFalse( logicImpl.canWriteItem(tdp.maintitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.USER_ID) );
      Assert.assertTrue( logicImpl.canWriteItem(tdp.item1, FakeDataPreload.LOCATION1_ID, FakeDataPreload.USER_ID) );

      // testing perms as user with special perms
      Assert.assertFalse( logicImpl.canWriteItem(tdp.adminitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.MAINT_USER_ID) );
      Assert.assertTrue( logicImpl.canWriteItem(tdp.maintitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.MAINT_USER_ID) );
      Assert.assertTrue( logicImpl.canWriteItem(tdp.item1, FakeDataPreload.LOCATION1_ID, FakeDataPreload.MAINT_USER_ID) );

      // testing perms as admin user
      Assert.assertTrue( logicImpl.canWriteItem(tdp.adminitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.ADMIN_USER_ID) );
      Assert.assertTrue( logicImpl.canWriteItem(tdp.maintitem, FakeDataPreload.LOCATION1_ID, FakeDataPreload.ADMIN_USER_ID) );
      Assert.assertTrue( logicImpl.canWriteItem(tdp.item1, FakeDataPreload.LOCATION1_ID, FakeDataPreload.ADMIN_USER_ID) );
   }

   /**
    * ScormCloud method for {@link org.sakaiproject.scormcloud.logic.impl.ScormCloudLogicImpl#getAllVisibleItems(java.lang.String, java.lang.String)}.
    */
   public void testGetAllVisibleItemsStringString() {

      // add 2 items to test if we can see the visible one and not the hidden one
      ScormCloudItem itemHidden = new ScormCloudItem("New item title", 
            FakeDataPreload.MAINT_USER_ID, FakeDataPreload.LOCATION1_ID, Boolean.TRUE, new Date());
      logicImpl.saveItem(itemHidden);
      ScormCloudItem itemVisible = new ScormCloudItem("New item title", 
            FakeDataPreload.MAINT_USER_ID, FakeDataPreload.LOCATION1_ID, Boolean.FALSE, new Date());
      logicImpl.saveItem(itemVisible);

      List<ScormCloudItem> l = logicImpl.getAllVisibleItems(FakeDataPreload.LOCATION1_ID, FakeDataPreload.USER_ID); // test normal user
      Assert.assertNotNull(l);
      Assert.assertEquals(4, l.size());
      Assert.assertTrue(l.contains(tdp.item1));
      Assert.assertTrue(! l.contains(tdp.item2));
      Assert.assertTrue(l.contains(itemVisible));
      Assert.assertTrue(! l.contains(itemHidden));

      List<ScormCloudItem> lmaintain = logicImpl.getAllVisibleItems(FakeDataPreload.LOCATION1_ID, FakeDataPreload.MAINT_USER_ID); // test maintainer
      Assert.assertNotNull(lmaintain);
      Assert.assertEquals(5, lmaintain.size());
      Assert.assertTrue(lmaintain.contains(tdp.item1));
      Assert.assertTrue(! lmaintain.contains(tdp.item2));
      Assert.assertTrue(lmaintain.contains(itemVisible));
      Assert.assertTrue(lmaintain.contains(itemHidden));

      List<ScormCloudItem> ladmin = logicImpl.getAllVisibleItems(FakeDataPreload.LOCATION1_ID, FakeDataPreload.ADMIN_USER_ID); // test admin
      Assert.assertNotNull(ladmin);
      Assert.assertEquals(5, ladmin.size());
      Assert.assertTrue(ladmin.contains(tdp.item1));
      Assert.assertTrue(! ladmin.contains(tdp.item2));
      Assert.assertTrue(ladmin.contains(itemVisible));
      Assert.assertTrue(ladmin.contains(itemHidden));
   }

   /**
    * ScormCloud method for {@link org.sakaiproject.scormcloud.logic.impl.ScormCloudLogicImpl#removeItem(org.sakaiproject.scormcloud.model.ScormCloudItem)}.
    */
   public void testRemoveItem() {
      try {
         logicImpl.removeItem(tdp.adminitem); // user cannot delete this
         Assert.fail("Should have thrown SecurityException");
      } catch (SecurityException e) {
         Assert.assertNotNull(e.getMessage());
      }

      try {
         logicImpl.removeItem(tdp.adminitem); // permed user cannot delete this
         Assert.fail("Should have thrown SecurityException");
      } catch (SecurityException e) {
         Assert.assertNotNull(e.getMessage());
      }

      logicImpl.removeItem(tdp.item1); // user can delete this
      ScormCloudItem item = logicImpl.getItemById(tdp.item1.getId());
      Assert.assertNull(item);
   }

   /**
    * ScormCloud method for {@link org.sakaiproject.scormcloud.logic.impl.ScormCloudLogicImpl#saveItem(org.sakaiproject.scormcloud.model.ScormCloudItem)}.
    */
   public void testSaveItem() {

      ScormCloudItem item = new ScormCloudItem("New item title", FakeDataPreload.USER_ID, FakeDataPreload.LOCATION1_ID, Boolean.FALSE, new Date());
      logicImpl.saveItem(item);
      Long itemId = item.getId();
      Assert.assertNotNull(itemId);

      // test saving an incomplete item
      ScormCloudItem incompleteItem = new ScormCloudItem();
      incompleteItem.setTitle("New incomplete item");
      incompleteItem.setHidden(Boolean.FALSE);

      // mock object is needed here
      logicImpl.saveItem(incompleteItem);

      Long incItemId = item.getId();
      Assert.assertNotNull(incItemId);

      item = logicImpl.getItemById(incItemId);
      Assert.assertNotNull(item);     
      Assert.assertEquals(item.getOwnerId(), FakeDataPreload.USER_ID);
      Assert.assertEquals(item.getLocationId(), FakeDataPreload.LOCATION1_ID);

      // test saving a null value for failure
      try {
         logicImpl.saveItem(null);
         Assert.fail("Should have thrown NullPointerException");
      } catch (NullPointerException e) {
         Assert.assertNotNull(e.getStackTrace());
      }
   }

}
