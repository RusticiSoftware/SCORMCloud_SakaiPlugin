/******************************************************************************
 * ExternalLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.scormcloud.logic;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.api.AssignmentSubmissionEdit;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * This is the implementation for logic which is external to our app logic
 * @author Sakai App Builder -AZ
 */
public class ExternalLogicImpl implements ExternalLogic {

	private static Log log = LogFactory.getLog(ExternalLogicImpl.class);

	private FunctionManager functionManager;
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	private ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	private SecurityService securityService;
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	private SessionManager sessionManager;
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	private SiteService siteService;
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}
	
	private GradebookExternalAssessmentService gradeBookExternalAssessmentService;
	public void setGradebookExternalAssessmentService(GradebookExternalAssessmentService service){
	    this.gradeBookExternalAssessmentService = service;
	}
	
	private AssignmentService assignmentService;
	public void setAssignmentService(AssignmentService service){
	    this.assignmentService = service;
	}


	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
		// register Sakai permissions for this tool
		functionManager.registerFunction(ITEM_WRITE_ANY);
		functionManager.registerFunction(ITEM_READ_HIDDEN);
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.scormcloud.logic.ExternalLogic#getCurrentLocationId()
	 */
   public String getCurrentLocationId() {
      String location = null;
      try {
         String context = toolManager.getCurrentPlacement().getContext();
         location  = context;
         Site s = siteService.getSite( context );
         location = s.getReference(); // get the entity reference to the site
      } catch (Exception e) {
         // sakai failed to get us a location so we can assume we are not inside the portal
         //return NO_LOCATION;
          return null;
      }
      /*if (location == null) {
         location = NO_LOCATION;
      }*/
      return location;
   }

	/* (non-Javadoc)
	 * @see org.sakaiproject.scormcloud.logic.ExternalLogic#getLocationTitle(java.lang.String)
	 */
	public String getLocationTitle(String locationId) {
	   String title = null;
		try {
			Site site = siteService.getSite(locationId);
			title = site.getTitle();
		} catch (IdUnusedException e) {
			log.warn("Cannot get the info about locationId: " + locationId);
			title = "----------";
		}
		return title;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.scormcloud.logic.ExternalLogic#getCurrentUserId()
	 */
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}
	
	public String getUserDisplayId(String userId) {
	    String displayId = null;
        try {
            displayId = userDirectoryService.getUser(userId).getDisplayId();
        } catch (UserNotDefinedException e) {
            log.warn("Cannot get user display id for id: " + userId);
            displayId = "--------";
        }
        return displayId;
	}

	public String getUserDisplayName(String userId) {
	   String name = null;
		try {
			name = userDirectoryService.getUser(userId).getDisplayName();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get user displayname for id: " + userId);
			name = "--------";
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.scormcloud.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		return securityService.isSuperUser(userId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.scormcloud.logic.ExternalLogic#isUserAllowedInLocation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
		if ( securityService.unlock(userId, permission, locationId) ) {
			return true;
		}
		return false;
	}
	
	public boolean isGradebookAvailable() {
	    String context = toolManager.getCurrentPlacement().getContext();
	    return gradeBookExternalAssessmentService.isGradebookDefined(context);
	}
	
	public void addGrade(String context, String gradeId, String externalUrl,
            String title, Double points, Date dueDate, String externalServiceDescription, Boolean ungraded){
	    if(gradeBookExternalAssessmentService.isGradebookDefined(context)){
    	    if(!gradeBookExternalAssessmentService.isAssignmentDefined(context, gradeId)){
    	        gradeBookExternalAssessmentService.addExternalAssessment(context, gradeId, externalUrl, title, points, dueDate, externalServiceDescription, ungraded);
    	    } else {
    	        gradeBookExternalAssessmentService.updateExternalAssessment(context, gradeId, externalUrl, title, points, dueDate, ungraded);
    	    }
	    }
	}
    public void updateGrade(String context, String gradeId, String externalUrl,
            String title, Double points, Date dueDate, String externalServiceDescription, Boolean ungraded){
        gradeBookExternalAssessmentService.updateExternalAssessment(context, gradeId, externalUrl, title, points, dueDate, ungraded);
    }
    public void deleteGrade(String context, String gradeId){
        gradeBookExternalAssessmentService.removeExternalAssessment(context, gradeId);
    }
    
    public void addScore(String context, String gradeId, String userId, String score){
        if(gradeBookExternalAssessmentService.isGradebookDefined(context)){
            if(gradeBookExternalAssessmentService.isExternalAssignmentDefined(context, gradeId)){
                log.debug("external logic addScore: context = " + context + ", gradeId = " + gradeId + ", userId = " + userId + ", score = " + score);
                gradeBookExternalAssessmentService.updateExternalAssessmentScore(context, gradeId, userId, score);
            } else {
                log.debug("No external assignment found for context = " + 
                     context + ", gradeId = " + gradeId + ". Not recording a grade");
            }
        } else {
            log.error("Error in addScore, no grade book defined for context = " + context);
        }
    }
    
    public String getCurrentContext(){
        Placement cur = toolManager.getCurrentPlacement();
        if(cur == null){
            log.warn("In getCurrentContext, no current placement found, returning null context");
            return null;
        }
        return cur.getContext();
    }
    
    public void updateAssignmentScore(String context, String userId, String assignmentId, String score){
        try {
            User user = userDirectoryService.getUser(userId);
            AssignmentSubmission sub = assignmentService.getSubmission(assignmentId, user);
            if(sub != null){
                log.debug("Found assignment submission, updating...");
                AssignmentSubmissionEdit edit = assignmentService.editSubmission(sub.getReference());
                edit.setGrade(score);
                edit.setGraded(true);
                assignmentService.commitEdit(edit);
                log.debug("Committed new score: " + score);
            }       
        } catch (Exception e){
            log.debug("Exception thrown in debugAssignment", e);
        }
    }
    
    public String getAssignmentIdFromAssignmentKey(String context, String userId, String assignmentKey) {
        try {
            log.debug("Finding all assignments for context " + context + " userId " + userId);
            Iterator it = assignmentService.getAssignmentsForContext(context, userId);
            while(it.hasNext()){
                Assignment asn = (Assignment)it.next();
                log.debug("Assignment with id " + asn.getId());
                AssignmentContent content = asn.getContent();
                List atts = content.getAttachments();
                for (Object att : atts){
                    String refId = ((Reference)att).getId();
                    log.debug("\tContent attachment reference: " + refId);
                    if(refId.contains(assignmentKey)){
                        log.debug("\tFound the assignment associated with this unique key!");
                        return asn.getId();
                    }
                }
            }
            log.debug("Couldn't find an assignment with an attachment matching assignmentKey = " + assignmentKey);
            return null;
        } catch (Exception e) {
            log.error("Exception thrown in getAssignmentIdFromAssignmentKey, returning null", e);
            return null;
        }
    }

}
