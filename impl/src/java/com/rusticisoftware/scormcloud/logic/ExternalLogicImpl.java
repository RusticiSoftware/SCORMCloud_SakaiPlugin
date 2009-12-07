/*
 *   Copyright 2009-2010 Rustici Software. Licensed under the
 *   Educational Community License, Version 2.0 (the "License"); you may
 *   not use this file except in compliance with the License. You may
 *   obtain a copy of the License at
 *   
 *   http://www.osedu.org/licenses/ECL-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package com.rusticisoftware.scormcloud.logic;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContent;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.StringUtil;

import com.rusticisoftware.scormcloud.logic.ExternalLogic;


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
	
	private AssignmentService assignmentService;
	public void setAssignmentService(AssignmentService service){
	    this.assignmentService = service;
	}
	
	private EventTrackingService eventTrackingService;
	public void setEventTrackingService(EventTrackingService eventTrackingService){
	    this.eventTrackingService = eventTrackingService;
	}

	private GradebookExternalAssessmentService gbExternal;
    public void setGradebookExternalAssessmentService(GradebookExternalAssessmentService service){
        this.gbExternal = service;
    }
    
	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
		// register Sakai permissions for this tool
		functionManager.registerFunction(SCORMCLOUD_CONFIGURE);
		functionManager.registerFunction(SCORMCLOUD_ADMIN);
		
	}
	
	public void registerEventObserver(Observer obs){
	    eventTrackingService.addObserver(obs);
	}


	/* (non-Javadoc)
	 * @see com.rusticisoftware.scormcloud.logic.ExternalLogic#getCurrentLocationId()
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
	 * @see com.rusticisoftware.scormcloud.logic.ExternalLogic#getLocationTitle(java.lang.String)
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
	 * @see com.rusticisoftware.scormcloud.logic.ExternalLogic#getCurrentUserId()
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
	 * @see com.rusticisoftware.scormcloud.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		return securityService.isSuperUser(userId);
	}

	/* (non-Javadoc)
	 * @see com.rusticisoftware.scormcloud.logic.ExternalLogic#isUserAllowedInLocation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
		if ( securityService.unlock(userId, permission, locationId) ) {
			return true;
		}
		return false;
	}
	
    
    public String getCurrentContext(){
        Placement cur = toolManager.getCurrentPlacement();
        if(cur == null){
            log.warn("In getCurrentContext, no current placement found, returning null context");
            return null;
        }
        return cur.getContext();
    }
    
    
    
    public void updateAssignmentScore(Assignment asn, String userId, String score){

        String addToGradebook = asn.getProperties().getProperty(AssignmentService.NEW_ASSIGNMENT_ADD_TO_GRADEBOOK);
        boolean shouldBeInGradebook = !(AssignmentService.GRADEBOOK_INTEGRATION_NO.equals(addToGradebook));

        if(!shouldBeInGradebook){
            log.debug("Assignment with id " + asn.getId() + 
                      " shouldn't even be in the gradebook, returning...");
            return;
        }
        
        String gradebookUid = asn.getContext();
        String assignmentRef = asn.getReference();
        String associateAssignmentRef = 
            StringUtil.trimToNull(asn.getProperties().getProperty(
                    AssignmentService.PROP_ASSIGNMENT_ASSOCIATE_GRADEBOOK_ASSIGNMENT));

        Double scoreDbl = 0.0;
        try {
            scoreDbl = Double.parseDouble(score);
        } catch (Exception e) {}
        
        
        boolean isExternalAssignmentDefined = gbExternal.isExternalAssignmentDefined(gradebookUid, assignmentRef);
        boolean isExternalAssociateAssignmentDefined = gbExternal.isExternalAssignmentDefined(
                                                            gradebookUid, associateAssignmentRef);
        
        //If this assignment isn't associated with some other designated gradebook entry,
        //ensure that it's own entry exists, and update the score for it
        if(associateAssignmentRef == null){
            if (!isExternalAssignmentDefined) {
                // add assignment to gradebook
                gbExternal.addExternalAssessment(
                        gradebookUid, assignmentRef, null, asn.getTitle(), 
                        ((double)asn.getContent().getMaxGradePoint()/10.0), 
                        new Date(asn.getDueTime().getTime()), "Assignments");
            }
            gbExternal.updateExternalAssessmentScore(gradebookUid, assignmentRef, userId, scoreDbl);
        }
        //Else this assignment is attached to some other existing gradebook entry
        else {
            if (isExternalAssociateAssignmentDefined) {
                gbExternal.updateExternalAssessmentScore(gradebookUid, associateAssignmentRef, userId, scoreDbl);
            }
        }
    }
     
    public Assignment getAssignmentFromAssignmentKey(String context, String userId, String assignmentKey) {
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
                        return asn;
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

    public boolean canSubmitAssignment(String context, String assignmentId){
        try {
            Assignment asn = assignmentService.getAssignment(assignmentId);
            if(asn == null){
                log.debug("In canSubmitAssignment, found no assignment with id = " + assignmentId);
                return false;
            }
            
            boolean canSubmit = assignmentService.canSubmit(context, asn);
            log.debug("canSubmitAssignment(" + context + ", " + context + ") = " + canSubmit);
            return canSubmit;
            
        } catch (Exception e){
            log.debug("Exception in canSubmitAssignment(" + context + "," + assignmentId +")", e);
            return true;
        }
    }
    
}
