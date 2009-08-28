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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.api.AssignmentSubmissionEdit;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;


import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.dao.ScormCloudDao;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;

import com.rusticisoftware.hostedengine.client.Configuration;
import com.rusticisoftware.hostedengine.client.RegistrationSummary;
import com.rusticisoftware.hostedengine.client.ScormEngineService;

/**
 * This is the implementation of the business logic interface
 * 
 * @author Sakai App Builder -AZ
 */
public class ScormCloudLogicImpl implements ScormCloudLogic, Observer {

    private static Log log = LogFactory.getLog(ScormCloudLogicImpl.class);
    
    

    private ScormCloudDao dao;

    public void setDao(ScormCloudDao dao) {
        this.dao = dao;
    }

    private ExternalLogic externalLogic;

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }


    private ScormCloudConfiguration scormCloudConfiguration;
    private ScormEngineService scormEngineService;

    /**
     * Place any code that should run when this class is initialized by spring
     * here
     */
    public void init() {
        log.debug("init");
        initScormEngineService();
        externalLogic.registerEventObserver(this);
    }
    
    
    
    //----------------- SCORM Cloud Configuration --------------------
    
    public void initScormEngineService(){
        scormCloudConfiguration = lookupScormCloudConfig();
        if(scormCloudConfiguration != null){
            log.debug("Found cloud config in database, initializing scorm engine service");
            Configuration config = 
                new Configuration(scormCloudConfiguration.getServiceUrl(), 
                                  scormCloudConfiguration.getAppId(),
                                  scormCloudConfiguration.getSecretKey());
            scormEngineService = new ScormEngineService(config);
        } else {
            log.debug("Couldn't find existing config, not initializing scorm engine service");
        }
    }
    
    private ScormCloudConfiguration lookupScormCloudConfig(){
        List<ScormCloudConfiguration> configs = dao.findAll(ScormCloudConfiguration.class);
        if(configs.size() > 0){
            return configs.get(0);
        }
        return null;
    }
    
    public boolean isScormEngineServiceInitialized(){
        return (scormEngineService != null);
    }
    
    public void setScormCloudConfiguration(ScormCloudConfiguration config){
        log.debug("setScormCloudConfiguration called w/ appId = " + config.getAppId());
        ScormCloudConfiguration existingConfig = lookupScormCloudConfig();
        if(existingConfig != null){
            log.debug("Existing config found, updating it");
            existingConfig.copyFrom(config);
            dao.save(existingConfig);
        } else {
            log.debug("No existing config found, adding new one");
            dao.save(config);
        }
        initScormEngineService();
    }
    
    public ScormCloudConfiguration getScormCloudConfiguration(){
        if(scormCloudConfiguration != null){
            if(externalLogic.isUserAdmin(externalLogic.getCurrentUserId())){
                return scormCloudConfiguration;
            } else {
                ScormCloudConfiguration copy = new ScormCloudConfiguration(scormCloudConfiguration);
                copy.setSecretKey(null);
                return copy;
            }
        }
        return null;
    }

    
    
    
    
    
    
    //--------------------- Packages -------------------------
    
    public ScormCloudPackage getPackageById(String id) {
        log.debug("Getting package by id: " + id);
        return dao.findById(ScormCloudPackage.class, id);
    }

    public void removePackage(ScormCloudPackage pkg) {
        log.debug("In removePackage with item:" + 
                  pkg.getId() + ":" + pkg.getTitle());
        // check if current user can remove this item
        try {
            scormEngineService
                .getCourseService()
                .DeleteCourse(pkg.getScormCloudId());
        } catch (Exception e) {
            log.debug(
                    "Exception occurred trying to delete package with id = "
                            + pkg.getId() + ", cloud id = "
                            + pkg.getScormCloudId(), e);
        }
        dao.delete(pkg);
        log.info("Removing package: " + pkg.getId() + ":" + pkg.getTitle());
    }

    public void addNewPackage(ScormCloudPackage pkg, File packageZip)
            throws Exception {
        log.debug("In addNewPackage with package:" + pkg.getTitle());
        // set the owner and site to current if they are not set
        if (pkg.getOwnerId() == null) {
            pkg.setOwnerId(externalLogic.getCurrentUserId());
        }
        if (pkg.getLocationId() == null) {
            pkg.setLocationId(externalLogic.getCurrentLocationId());
        }
        if (pkg.getContext() == null) {
            pkg.setContext(externalLogic.getCurrentContext());
        }
        if (pkg.getDateCreated() == null) {
            pkg.setDateCreated(new Date());
        }
        // save pkg if new OR check if the current user can update the existing pkg
        if (pkg.getId() == null) {
            scormEngineService.getCourseService().ImportCourse(
                    pkg.getScormCloudId(), packageZip.getAbsolutePath());
            dao.save(pkg);
            log.info("Saved package: " + pkg.getId() + ":" + pkg.getTitle());
        } else {
            throw new SecurityException("Current user cannot update package "
                    + pkg.getId() + " because they do not have permission");
        }
    }

    public void updatePackage(ScormCloudPackage pkg) {
        log.debug("In saveItem with item:" + pkg.getTitle());
        // set the owner and site to current if they are not set
        if (pkg.getOwnerId() == null) {
            pkg.setOwnerId(externalLogic.getCurrentUserId());
        }
        if (pkg.getLocationId() == null) {
            pkg.setLocationId(externalLogic.getCurrentLocationId());
        }
        if (pkg.getContext() == null){
            pkg.setContext(externalLogic.getCurrentContext());
        }
        if (pkg.getDateCreated() == null) {
            pkg.setDateCreated(new Date());
        }
        // save pkg if new OR check if the current user can update the existing
        // pkg
        dao.save(pkg);
        log.info("Saving package: " + pkg.getId() + ":" + pkg.getTitle());
    }
    
    public String getPackagePropertiesUrl(ScormCloudPackage pkg){
        try {
            return scormEngineService
                    .getCourseService()
                        .GetPropertyEditorUrl(pkg.getScormCloudId());
        } catch (Exception e) {
            log.error("Encountered exception while trying to get " +
                      "property editor url for package with SCORM " +
                      "cloud id = " + pkg.getScormCloudId(), e);
            return null;
        }
    }
    
    public String getPackagePreviewUrl(ScormCloudPackage pkg, String redirectOnExitUrl){
        try {
            return scormEngineService.getCourseService()
                        .GetPreviewUrl(pkg.getScormCloudId(), redirectOnExitUrl);
        } catch (Exception e) {
            log.error("Encountered an exception while trying to get " +
                      "preview url from SCORM Cloud", e);
          return null;
       }
    }


    
    

    
    
    //---------------------- Registrations ---------------------------
    
    public ScormCloudRegistration addNewRegistration(ScormCloudPackage pkg,
            String userId, String assignmentKey) {
        String userDisplayName = externalLogic.getUserDisplayName(userId);
        String userDisplayId = externalLogic.getUserDisplayId(userId);
        Assignment assignment = externalLogic.getAssignmentFromAssignmentKey(
                                            pkg.getContext(), userId, assignmentKey);
        
        int contributingResources = getNumberOfContributingResourcesForAssignment(assignment);
        
        String firstName = "sakai";
        String lastName = "learner";
        
        if (userDisplayName != null && userDisplayName.contains(" ")) {
            String[] nameParts = userDisplayName.split(" ");
            firstName = nameParts[0];
            lastName = nameParts[1];
        }

        try {
            String cloudRegId = "sakai-reg-" + userDisplayId + "-" + UUID.randomUUID().toString();
            scormEngineService.getRegistrationService().CreateRegistration(
                    cloudRegId, pkg.getScormCloudId(), userId, firstName, lastName);
            
            ScormCloudRegistration reg = new ScormCloudRegistration();
            reg.setDateCreated(new Date());
            reg.setOwnerId(userId);
            reg.setLocationId(pkg.getLocationId());
            reg.setContext(pkg.getContext());
            
            reg.setUserName(userDisplayId);
            reg.setScormCloudId(cloudRegId);
            reg.setPackageId(pkg.getId());
            
            reg.setAssignmentKey(assignmentKey);
            reg.setAssignmentId(assignment.getId());
            reg.setAssignmentName(assignment.getTitle());
            reg.setContributesToAssignmentGrade(pkg.getContributesToAssignmentGrade());
            reg.setNumberOfContributingResources(contributingResources);
            
            dao.save(reg);
            return reg;
        } catch (Exception e) {
            log.debug("exception thrown creating reg", e);
            return null;
        }
    }
    
    
    public ScormCloudRegistration getRegistrationById(String id) {
        log.debug("Getting registration with id: " + id);
        return dao.findById(ScormCloudRegistration.class, id);
    }
    
    
    public List<ScormCloudRegistration> getRegistrationsByPackageId(String packageId) {
        log.debug("Getting registrations for package with id = " + packageId);
        ScormCloudPackage pkg = this.getPackageById(packageId);
        if(pkg == null){
            log.debug("Error finding registrations for package, " +
                      "no package found with id = " + packageId);
            return null;
        }
        Search s = new Search();
        s.addRestriction(new Restriction("packageId", packageId));
        return dao.findBySearch(ScormCloudRegistration.class, s);
    }

    
    
    public ScormCloudRegistration findRegistrationFor(String userId, String assignmentKey) {
        log.debug("Finding registration with userId = " + userId
                + ", assignmentKey = " + assignmentKey);
        
        Search s = new Search();
        s.addRestriction(new Restriction("ownerId", userId));
        s.addRestriction(new Restriction("assignmentKey", assignmentKey));
        List<ScormCloudRegistration> regs = dao.findBySearch(ScormCloudRegistration.class, s);
        
        if (regs.size() >= 1) {
            if (regs.size() > 1) {
                log.warn("Found more than one registration with userId = "
                        + userId + " and assignmentKey = " + assignmentKey);
            }
            return regs.get(0);
        }
        log.debug("Couldn't find any regs, returning null");
        return null;
    }
    
    private List<ScormCloudRegistration> findContributingRegistrationsFor(String userId, String assignmentId){
        Search s = new Search();
        s.addRestriction(new Restriction("ownerId", userId));
        s.addRestriction(new Restriction("assignmentId", assignmentId));
        s.addRestriction(new Restriction("contributesToAssignmentGrade", Boolean.TRUE));
        return dao.findBySearch(ScormCloudRegistration.class, s);
    }


    public void removeRegistration(ScormCloudRegistration reg) {
        log.debug("In removeRegistration with regId:" + reg.getId());
        // check if current user can remove this item
        try {
            scormEngineService.getRegistrationService().DeleteRegistration(reg.getScormCloudId());
        }
        catch (Exception e){
            log.debug("Exception thrown trying to delete registration with id = " +
                      reg.getId() + ", cloud id = " + reg.getScormCloudId(), e);
        }
        dao.delete(reg);
        log.info("Removing reg with id: " + reg.getId());
    }
    
    public void resetRegistration(ScormCloudRegistration reg) {
        log.debug("In resetRegistration with regId:" + reg.getId());
        // check if current user can remove this item
        try {
            log.info("Resetting reg with id: " + reg.getId());
            scormEngineService.getRegistrationService().ResetRegistration(reg.getScormCloudId());
            reg.setComplete("unknown");
            reg.setSuccess("unknown");
            reg.setScore("unknown");
            reg.setTotalTime("0");
            dao.save(reg);
        }
        catch (Exception e){
            log.debug("Exception thrown trying to reset registration with id = " +
                      reg.getId() + ", cloud id = " + reg.getScormCloudId(), e);
        }
    }

    /*public void updateRegistration(ScormCloudRegistration reg) {
        log.debug("In updateRegistration with reg id:" + reg.getId());
        // set the owner and site to current if they are not set
        if (reg.getOwnerId() == null) {
            reg.setOwnerId(externalLogic.getCurrentUserId());
        }
        if (reg.getLocationId() == null) {
            reg.setLocationId(externalLogic.getCurrentLocationId());
        }
        if (reg.getContext() == null) {
            reg.setContext(externalLogic.getCurrentContext());
        }
        if (reg.getDateCreated() == null) {
            reg.setDateCreated(new Date());
        }
        // save pkg if new OR check if the current user can update the existing pkg
        if (canWriteRegistration(reg, externalLogic.getCurrentLocationId(),
                externalLogic.getCurrentUserId())) {
            dao.save(reg);
            log.info("Saving registration: " + reg.getId());
        } else {
            throw new SecurityException("Current user cannot update package "
                    + reg.getId() + " because they do not have permission");
        }
    }*/
    
    

    public void updateRegistrationResultsFromCloud(ScormCloudRegistration reg) {
        if (reg == null){
            log.debug("Error: registration passed to " +
                      "updateRegistrationResultsFromCloud was null!");
            return;
        }
        try {
            log.debug("Updating registration " + reg.getId() + " with results from SCORM Cloud...");
            RegistrationSummary sum = scormEngineService
                                        .getRegistrationService()
                                        .GetRegistrationSummary(
                                                reg.getScormCloudId());
            reg.setComplete(sum.getComplete());
            reg.setSuccess(sum.getSuccess());
            reg.setScore(sum.getScore());
            reg.setTotalTime(sum.getTotalTime());
            dao.save(reg);
        } catch (Exception e) {
            log.debug("Exception getting registration results for " +
                      "reg with id = " + reg.getId() + 
                      " cloud id = " + reg.getScormCloudId(), e);
        }
    }
    
    
    public String getLaunchUrl(ScormCloudRegistration reg, String redirectOnExitUrl) {
        try {
            return scormEngineService.getRegistrationService()
                        .GetLaunchUrl(reg.getScormCloudId(), redirectOnExitUrl);
        } catch (Exception e) {
            log.error("Encountered an exception while trying to get " +
                      "launch url from SCORM Cloud, returning null", e);
            return null;
        }
    }
    
    
    public Double getScoreFromRegistration(ScormCloudRegistration reg){
        Double score = null;
        try { 
            score = new Double(reg.getScore()); 
            //The need for this multiplication may disappear in the future
            //At which point the returned score will already be scaled to 100
            if(score <= 1.0){
                score *= 100.0;
            }
        }
        catch (NumberFormatException nfe) {}
        return (score == null) ? 0.0 : score; 
    }
    
    
    public Double getCombinedScore(List<ScormCloudRegistration> regs){
        if (regs.size() == 0){
            log.debug("getCombinedScore called with no regs, returning 0.0");
            return 0.0;
        }
        
        //Grab number of contributing regs / resources for the assignment
        ScormCloudRegistration firstReg = regs.get(0);
        String assignmentId = firstReg.getAssignmentId();
        int numberOfContributingResources = firstReg.getNumberOfContributingResources();
        
        log.debug("In getCombinedScoreForAssignment, found " + regs.size() + 
                  " regs and " + numberOfContributingResources + " contributing " +
                  " resources associated with assignment = " + assignmentId);

        Double scoreSum = 0.0;
        for (ScormCloudRegistration r : regs){
            scoreSum += getScoreFromRegistration(r);
        }
        
        //This assumes all contributing registrations / resources are weighted equally
        return scoreSum / numberOfContributingResources;
    }
    
    
    
    
    
    
    //------------------ Assignment Integration Methods --------------------

    public void update(Observable o, Object args){
        Event evt = (Event)args;
        log.debug("SCORM_CLOUD_LOGIC_EVENT_OBSERVATION: " + 
                  " event = " + evt.getEvent() +
                  " context = " + evt.getContext() + 
                  " userId = " + evt.getUserId() +
                  " resource = " + evt.getResource());
        
        try {
            if ("asn.submit.submission".equals(evt.getEvent())){
                //This may not find any associated contributing registrations, 
                //in which case, it won't update the score of the submission
                AssignmentSubmission sub = AssignmentService.getSubmission(evt.getResource());
                updateAssignmentScoreFromRegistrationScores(
                        sub.getAssignment(), externalLogic.getCurrentUserId(), true);
            }
        } catch (Exception e){
            log.error("Exception thrown in update", e);
        }
    }
    
    private int getNumberOfContributingResourcesForAssignment(Assignment asn){
        int count = 0;
        List attachments = asn.getContent().getAttachments();
        log.debug("\tIn getNumberOfActiveCloudPackageResourcesForAssignment, total attachments? = " + attachments.size());
        for (Object att : attachments){
            String pkgId = ((Reference)att).getEntity().getProperties().getProperty("packageId");
            ScormCloudPackage pkg = getPackageById(pkgId);
            if(pkg.getContributesToAssignmentGrade()){
                count++;
            }
        }
        log.debug("Total number of contributing resources for this assignment? " + count);
        return count;
    }
    
    public void updateAssignmentScoreFromRegistrationScores(Assignment asn, String userId, boolean updateRegsFromCloud){
        String assignmentId = asn.getId();

        log.debug("Updating assignment score for assignment " + assignmentId);
        List<ScormCloudRegistration> regs = findContributingRegistrationsFor(userId, assignmentId);
        if(regs.size() == 0){
            log.debug("No contributing regisrations found for assignment = " + assignmentId +
                      " therefore not updating it's score!");
            return;
        }
        
        if(updateRegsFromCloud){
            for(ScormCloudRegistration reg : regs){
                updateRegistrationResultsFromCloud(reg);
            }
        }
        
        double maxPoints = ((double)asn.getContent().getMaxGradePoint()/10.0);
        Double score = (getCombinedScore(regs) * maxPoints) / 100.0;
        String scoreStr = (score == null) ? null : score.toString();
        externalLogic.updateAssignmentScore(asn, userId, scoreStr);
    }



    public String getAssignmentNameFromId(String id) {
        return externalLogic.getAssignmentNameFromId(id);
    }


    public boolean isCurrentUserSakaiAdmin(){
        String currentUserId = externalLogic.getCurrentUserId();
        return externalLogic.isUserAdmin(currentUserId);
    }

    public boolean isCurrentUserPluginAdmin() {
        String currentUserId = externalLogic.getCurrentUserId();
        String currentLocation = externalLogic.getCurrentLocationId();
        return externalLogic.isUserAllowedInLocation(
                    currentUserId, ExternalLogic.SCORMCLOUD_ADMIN, currentLocation);
    }

    public boolean isPluginConfigured() {
        ScormCloudConfiguration configuration = getScormCloudConfiguration();
        return (configuration != null);
    }

    public boolean canConfigurePlugin() {
        //Sakai admins can do whatever they please...
        if(isCurrentUserSakaiAdmin()){
            return true;
        }
        String currentUserId = externalLogic.getCurrentUserId();
        String currentLocation = externalLogic.getCurrentLocationId();
        return externalLogic.isUserAllowedInLocation(
                currentUserId, ExternalLogic.SCORMCLOUD_CONFIGURE, currentLocation);
    }

    public List<ScormCloudPackage> getAllSitePackages(){
        Search s = new Search();
        s.addRestriction(new Restriction("locationId", externalLogic.getCurrentLocationId()));
        return dao.findBySearch(ScormCloudPackage.class, s);
    }
}
