package org.sakaiproject.scormcloud.model;

import java.io.Serializable;
import java.util.Date;

public class ScormCloudRegistration implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	//private String title;
	private String ownerId; // Sakai userId
	private String locationId; // Sakai locationId
	private Date dateCreated;
	private String scormCloudId;
	private String packageId;
	
	public ScormCloudRegistration () {
		
	}

	/**
	 * Minimal constructor
	 */
	public ScormCloudRegistration(String ownerId, String locationId) {
		this.ownerId = ownerId;
		this.locationId = locationId;
	}

	/**
	 * Full constructor
	 */
	public ScormCloudRegistration(String ownerId, String locationId, Date dateCreated) {
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
}
