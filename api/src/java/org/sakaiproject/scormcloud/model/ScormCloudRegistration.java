package org.sakaiproject.scormcloud.model;

import java.io.Serializable;
import java.util.Date;

public class ScormCloudRegistration implements Serializable {
    private static final long serialVersionUID = 1L;

	private String id;
	private String scormCloudId;
    private String packageId;
	private String ownerId; // Sakai userId
	private String locationId; // Sakai locationId
	private String context; //Sakai tool context id
	
	private Date dateCreated;
	private String userName;
	
	private String assignmentId; //assignment related to this reg
    private String assignmentKey; //assignment "context" related to resource attachment
    private String assignmentName;
    private Boolean contributesToAssignmentGrade;
	private Integer numberOfContributingResources = 0;
	
	private String complete;
	private String success;
	private String score;
	private String totalTime;
	
	
	public ScormCloudRegistration () {
		
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
	public String getAssignmentId(){
	    return assignmentId;
	}
	public void setAssignmentId(String assignmentId){
	    this.assignmentId = assignmentId;
	}
	public String getAssignmentKey(){
	    return assignmentKey;
	}
	public void setAssignmentKey(String assignmentKey){
	    this.assignmentKey = assignmentKey;
	}
	public String getAssignmentName(){
	    return assignmentName;
	}
	public void setAssignmentName(String assignmentName){
	    this.assignmentName = assignmentName;
	}
	public String getScormCloudId() {
		return scormCloudId;
	}
	public void setScormCloudId(String scormCloudId){
		this.scormCloudId = scormCloudId;
	}	
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getUserName() {
	    return userName;
	}
	public void setUserName(String userName) {
	    this.userName = userName;
	}
    public String getComplete() {
        return complete;
    }
    public void setComplete(String complete) {
        this.complete = complete;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public String getTotalTime() {
        return totalTime;
    }
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    public Integer getNumberOfContributingResources(){
        return numberOfContributingResources;
    }
    public void setNumberOfContributingResources(Integer contributingResources){
        this.numberOfContributingResources = contributingResources;
    }
    public Boolean getContributesToAssignmentGrade() {
        return contributesToAssignmentGrade;
    }
    public void setContributesToAssignmentGrade(Boolean contributes) {
        this.contributesToAssignmentGrade = contributes;
    }
}
