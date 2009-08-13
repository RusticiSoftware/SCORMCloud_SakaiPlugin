/******************************************************************************
 * ScormCloudBean.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.scormcloud.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
import org.sakaiproject.scormcloud.model.ScormCloudItem;

/**
 * This is the backing bean for actions related to ScormCloud Items
 * @author Sakai App Builder -AZ
 */
public class ScormCloudItemsBean {

	private static Log log = LogFactory.getLog(ScormCloudItemsBean.class);

	public ScormCloudItem newItem = new ScormCloudItem();
	public Map selectedIds = new HashMap();

	private ScormCloudLogic logic;
	public void setLogic(ScormCloudLogic logic) {
		this.logic = logic;
	}

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	public ArrayList messages = new ArrayList();

	public void init() {
		log.debug("init");
	}

	public ScormCloudItemsBean() {
		log.debug("constructor");
	}

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}

	/**
	 * @param item a ScormCloudItem to check
	 * @return true if the current user can remove or update the item
	 */
	public boolean canDelete(ScormCloudItem item) {
		log.debug("check Delete for: " + item.getId());
		return logic.canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
	}

	/**
	 * @return a List of ScormCloudItem objects visible to the current user in the current site
	 */
	public List getAllVisibleItems() {
		return logic.getAllVisibleItems(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
	}

	/**
	 * @param id the unique id of the item
	 * @return a ScormCloudItem or null if not found
	 */
	public ScormCloudItem getItemById(Long id) {
		return logic.getItemById(id);
	}

	public void addOrUpdateItem(ScormCloudItem item) {
		logic.saveItem(item);
	}

	/**
	 * @param item a ScormCloudItem to remove
	 * @return true if the item can be removed by the current user, false otherwise
	 */
	public boolean checkRemoveItemById(Long id) {
		log.debug("check and Remove for: " + id);
		ScormCloudItem item = logic.getItemById(id);
		if ( item != null && 
				logic.canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
			logic.removeItem(item);
			return true;
		}
		return false;
	}

}