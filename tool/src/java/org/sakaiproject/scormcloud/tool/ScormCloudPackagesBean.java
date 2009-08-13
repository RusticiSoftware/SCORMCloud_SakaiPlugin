package org.sakaiproject.scormcloud.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
import org.sakaiproject.scormcloud.model.ScormCloudItem;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;

public class ScormCloudPackagesBean {
	private static Log log = LogFactory.getLog(ScormCloudItemsBean.class);

	public ScormCloudPackage newItem = new ScormCloudPackage();
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
	public ArrayList getMessages() {
		return messages;
	}

	public void init() {
		log.debug("init");
	}

	public ScormCloudPackagesBean() {
		log.debug("constructor");
	}

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}

	/**
	 * @param item a ScormCloudPackage to check
	 * @return true if the current user can remove or update the item
	 */
	public boolean canDelete(ScormCloudPackage pkg) {
		log.debug("check Delete for: " + pkg.getId());
		return logic.canWritePackage(pkg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
	}

	/**
	 * @return a List of ScormCloudPackage objects visible to the current user in the current site
	 */
	public List getAllVisiblePackages() {
		return logic.getAllVisiblePackages(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
	}

	/**
	 * @param id the unique id of the item
	 * @return a ScormCloudPackage or null if not found
	 */
	public ScormCloudPackage getPackageById(String id) {
		return logic.getPackageById(id);
	}

	public void updatePackage(ScormCloudPackage pkg) {
		logic.updatePackage(pkg);
	}
	
	public void addNewPackage(ScormCloudPackage pkg, File zipFile) throws Exception {
		logic.addNewPackage(pkg, zipFile);
	}
	
	public String getLaunchUrl(ScormCloudPackage pkg){
		return logic.getLaunchUrl(pkg);
	}
	
	/**
	 * @param item a ScormCloudPackage to remove
	 * @return true if the item can be removed by the current user, false otherwise
	 */
	public boolean checkRemovePackageById(String id) {
		log.debug("check and Remove for: " + id);
		ScormCloudPackage item = logic.getPackageById(id);
		if ( item != null && 
				logic.canWritePackage(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
			logic.removePackage(item);
			return true;
		}
		return false;
	}
}
