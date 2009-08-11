package org.sakaiproject.scormcloud.model;

import java.io.Serializable;
import java.util.Date;

public class ScormCloudPackage  implements Serializable  {
private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String ownerId; // Sakai userId
	private String locationId; // Sakai locationId
	private Boolean hidden; // only visible to owner if true
	private Date dateCreated;

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
			Boolean hidden, Date dateCreated) {
		this.title = title;
		this.ownerId = ownerId;
		this.locationId = locationId;
		this.hidden = hidden;
		this.dateCreated = dateCreated;
	}

	/**
	 * Getters and Setters
	 */
	public Boolean getHidden() {
		return hidden;
	}
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}
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

}
