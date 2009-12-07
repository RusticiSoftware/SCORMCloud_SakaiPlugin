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


package com.rusticisoftware.scormcloud.model;

import java.io.Serializable;
import java.util.Date;

public class ScormCloudPackage  implements Serializable  {
private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String ownerId; // Sakai userId
	private String locationId; // Sakai locationId
	private String context; //Sakai tool context
	private Boolean contributesToAssignmentGrade;
	private Boolean allowLaunchOutsideAssignment;
	private Date dateCreated;
	
	private String scormCloudId;

	/**
	 * Default constructor
	 */
	public ScormCloudPackage() {
	}

	/**
	 * Minimal constructor
	 */
	public ScormCloudPackage(String title,
			String ownerId, String locationId) {
		this.title = title;
		this.ownerId = ownerId;
		this.locationId = locationId;
	}

	/**
	 * Full constructor
	 */
	public ScormCloudPackage(String title,
			String ownerId, String locationId,
			Date dateCreated) {
		this.title = title;
		this.ownerId = ownerId;
		this.locationId = locationId;
		this.dateCreated = dateCreated;
	}

	/**
	 * Getters and Setters
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getContext(){
	    return context;
	}
	public void setContext(String context){
	    this.context = context;
	}
	public String getScormCloudId() {
		return scormCloudId;
	}
	public void setScormCloudId(String scormCloudId){
		this.scormCloudId = scormCloudId;
	}
	public Boolean getContributesToAssignmentGrade() {
        return contributesToAssignmentGrade;
    }
    public void setContributesToAssignmentGrade(Boolean contributes) {
        this.contributesToAssignmentGrade = contributes;
    }
    public boolean getAllowLaunchOutsideAssignment() {
        return allowLaunchOutsideAssignment;
    }
    public void setAllowLaunchOutsideAssignment(Boolean allow){
        this.allowLaunchOutsideAssignment = allow;
    }

}
