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
import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;

public class ScormCloudPackagesBean {
	private static Log log = LogFactory.getLog(ScormCloudPackagesBean.class);

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
	/*public ScormCloudPackage getPackageById(String id) {
		return logic.getPackageById(id);
	}

	public void updatePackage(ScormCloudPackage pkg) {
		logic.updatePackage(pkg);
	}
	
	public void addNewPackage(ScormCloudPackage pkg, File zipFile) throws Exception {
		logic.addNewPackage(pkg, zipFile);
	}
	
	public String getPackagePropertiesUrl(ScormCloudPackage pkg) throws Exception {
	    return logic.getPackagePropertiesUrl(pkg);
	}
	
	public String getPackagePreviewUrl(ScormCloudPackage pkg, String redirectOnExitUrl) throws Exception {
	    return logic.getPackagePreviewUrl(pkg, redirectOnExitUrl);
	}*/
	
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
	
	
	
	
	
	
	
	
	//--------------Registrations-------------------

	/**
	 * @return a List of ScormCloudPackage objects visible to the current user in the current site
	 */
	/*public List getAllVisibleRegistrations() {
		return logic.getAllVisibleRegistrations(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
	}*/

	/**
	 * @param id the unique id of the item
	 * @return a ScormCloudPackage or null if not found
	 */
	/*public ScormCloudRegistration getRegistrationById(String id) {
		return logic.getRegistrationById(id);
	}
	
	public List<ScormCloudRegistration> getRegistrationsByPackageId(String packageId) {
	    return logic.getRegistrationsByPackageId(packageId);
	}
	
	public ScormCloudRegistration findOrCreateUserRegistrationFor(ScormCloudPackage pkg, String assignmentKey){
		ScormCloudRegistration reg = logic.findRegistrationFor(pkg.getId(), externalLogic.getCurrentUserId(), assignmentKey);
		if(reg == null){
			reg = logic.addNewRegistration(pkg, externalLogic.getCurrentUserId(), assignmentKey);
		}
		return reg;
	}
	
	public boolean canDelete(ScormCloudRegistration reg) {
        log.debug("check Delete for: " + reg.getId());
        return logic.canWriteRegistration(reg, 
                    externalLogic.getCurrentLocationId(), 
                    externalLogic.getCurrentUserId());
    }

	public void updateRegistration(ScormCloudRegistration reg) {
		logic.updateRegistration(reg);
	}
	
	public void updateRegistrationResultsFromCloud(ScormCloudRegistration reg) {
	    logic.updateRegistrationResultsFromCloud(reg);
	}
	
	public ScormCloudRegistration addNewRegistration(String userId, ScormCloudPackage pkg){
		return logic.addNewRegistration(userId, pkg);
	}
	
	public String getLaunchUrl(ScormCloudRegistration reg, String redirectOnExitUrl){
		return logic.getLaunchUrl(reg, redirectOnExitUrl);
	}*/
	
	/**
	 * @param item a ScormCloudPackage to remove
	 * @return true if the item can be removed by the current user, false otherwise
	 */
	public boolean checkRemoveRegistrationById(String id) {
		log.debug("check and Remove for: " + id);
		ScormCloudRegistration reg = logic.getRegistrationById(id);
		if ( reg != null && 
				logic.canWriteRegistration(reg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
			logic.removeRegistration(reg);
			return true;
		}
		return false;
	}
	
	public boolean checkResetRegistrationById(String id) {
        log.debug("check and Remove for: " + id);
        ScormCloudRegistration reg = logic.getRegistrationById(id);
        if ( reg != null && 
                logic.canWriteRegistration(reg, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
            logic.resetRegistration(reg);
            return true;
        }
        return false;
    }
	
	/*
	public void setConfiguration(ScormCloudConfiguration config){
	    logic.setScormCloudConfiguration(config);
	}
	public ScormCloudConfiguration getConfiguration(){
	    return logic.getScormCloudConfiguration();
	}*/
}
