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
import java.util.Observer;

import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentSubmission;


/**
 * This is the interface for logic which is external to our app logic
 * @author Sakai App Builder -AZ
 */
public interface ExternalLogic {

	public final static String NO_LOCATION = "noLocationAvailable";

	// permissions
	public final static String SCORMCLOUD_ADMIN = "scormcloud.admin";
	public final static String SCORMCLOUD_CONFIGURE = "scormcloud.configure";

	/**
	 * @return the current sakai user id (not username)
	 */
	public String getCurrentUserId();

	/**
	 * Get the display name for a user by their unique id
	 * @param userId the current sakai user id (not username)
	 * @return display name (probably firstname lastname) or "----------" (10 hyphens) if none found
	 */
	public String getUserDisplayName(String userId);

	/**
	 * @return the current location id of the current user
	 */
	public String getCurrentLocationId();

	/**
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @return the title for the context or "--------" (8 hyphens) if none found
	 */
	public String getLocationTitle(String locationId);

	/**
	 * Check if this user has super admin access
	 * @param userId the internal user id (not username)
	 * @return true if the user has admin access, false otherwise
	 */
	public boolean isUserAdmin(String userId);

	/**
	 * Check if a user has a specified permission within a context, primarily
	 * a convenience method and passthrough
	 * 
	 * @param userId the internal user id (not username)
	 * @param permission a permission string constant
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @return true if allowed, false otherwise
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId);
	
	
    public String getCurrentContext();

    public String getUserDisplayId(String userId);

    public void registerEventObserver(Observer obs);
    
    public Assignment getAssignmentFromAssignmentKey(String context, String userId, String assignmentKey);
    public void updateAssignmentScore(Assignment asn, String userId, String score);
    public boolean canSubmitAssignment(String context, String assignmentId);
}
