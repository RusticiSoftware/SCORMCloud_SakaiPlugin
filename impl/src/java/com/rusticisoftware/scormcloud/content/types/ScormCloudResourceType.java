/**********************************************************************************
 * $URL:  $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package com.rusticisoftware.scormcloud.content.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.content.api.ResourceTypeRegistry;
import org.sakaiproject.content.api.ResourceToolAction.ActionType;
import org.sakaiproject.content.util.BaseInteractionAction;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;

import com.rusticisoftware.scormcloud.logic.ScormCloudLogic;


public class ScormCloudResourceType extends BaseResourceType {
	
    protected static Log log = LogFactory.getLog(ScormCloudResourceType.class);
    
    public static final String PROP_SCORMCLOUD_PACKAGE_ID = "packageId";
			
	public static final String SCORM_CONTENT_LABEL="SCORM Cloud Package";
	public static final String SCORM_CONTENT_TYPE_ID="scormcloud.type";
	
	public static final String SCORM_UPLOAD_LABEL="Upload SCORM Package";
	public static final String SCORM_CONFIGURE_LABEL="Configure";
	public static final String SCORM_PREVIEW_LABEL="Preview";
	public static final String SCORM_REMOVE_LABEL="Remove";

	public static final String SCORM_PREVIEW_ACTION_ID="scormcloud.preview";
	public static final String SCORM_CONFIGURE_ACTION_ID="scormcloud.configure";
	
	public static final String SCORM_UPLOAD_HELPER_ID="sakai.scormcloud.upload.helper"; 
	public static final String SCORM_CONFIGURE_HELPER_ID="sakai.scormcloud.configure.helper";
	public static final String SCORM_PREVIEW_HELPER_ID="sakai.scormcloud.preview.helper";
    public static final String SCORM_REMOVE_HELPER_ID="sakai.scormcloud.remove.helper";
	
    protected ResourceTypeRegistry resourceTypeRegistry;
    public void setResourceTypeRegistry(ResourceTypeRegistry registry){
        this.resourceTypeRegistry = registry;
    }
    
    protected ScormCloudLogic scormCloudLogic;
    public void setScormCloudLogic(ScormCloudLogic scormCloudLogic){
        this.scormCloudLogic = scormCloudLogic;
    }
    
	public ScormCloudResourceType() {	
		
	}
	
	public void init(){
	    List<String> requiredKeys = new ArrayList<String>();
        requiredKeys.add(ResourceProperties.PROP_STRUCTOBJ_TYPE);
        requiredKeys.add(ContentHostingService.PROP_ALTERNATE_REFERENCE);
          
        ResourceToolAction create = new BaseInteractionAction(ResourceToolAction.CREATE, ResourceToolAction.ActionType.NEW_UPLOAD, SCORM_CONTENT_TYPE_ID, SCORM_UPLOAD_HELPER_ID, requiredKeys) {
            public String getLabel() 
            {
                return SCORM_UPLOAD_LABEL; 
            }
            
            public void finalizeAction(Reference reference, String initializationId) {
                log.warn("Finalizing upload action for SCORM Cloud content!");
            }
            
            public boolean available(ContentEntity entity){
                return scormCloudLogic.isCurrentUserSakaiAdmin() || scormCloudLogic.isCurrentUserPluginAdmin();
            }
        };   
        
        
        ResourceToolAction remove = new BaseInteractionAction(ResourceToolAction.DELETE, ResourceToolAction.ActionType.DELETE, SCORM_CONTENT_TYPE_ID, SCORM_REMOVE_HELPER_ID, requiredKeys) {
            public String getLabel() {
                return SCORM_REMOVE_LABEL;
            }
            public boolean available(ContentEntity entity){
                return scormCloudLogic.isCurrentUserSakaiAdmin() || scormCloudLogic.isCurrentUserPluginAdmin();
            }
        };
        
        ResourceToolAction configure = new BaseInteractionAction(SCORM_CONFIGURE_ACTION_ID, ResourceToolAction.ActionType.VIEW_CONTENT, SCORM_CONTENT_TYPE_ID, SCORM_CONFIGURE_HELPER_ID, requiredKeys){
            public String getLabel() {
                return SCORM_CONFIGURE_LABEL;
            }
            public boolean available(ContentEntity entity){
                return scormCloudLogic.isCurrentUserSakaiAdmin() || scormCloudLogic.isCurrentUserPluginAdmin();
            }
        };
        
        ResourceToolAction preview = new BaseInteractionAction(SCORM_PREVIEW_ACTION_ID, ResourceToolAction.ActionType.VIEW_CONTENT, SCORM_CONTENT_TYPE_ID, SCORM_PREVIEW_HELPER_ID, requiredKeys){
            public String getLabel() {
                return SCORM_PREVIEW_LABEL;
            }
            public boolean available(ContentEntity entity){
                return scormCloudLogic.isCurrentUserSakaiAdmin() || scormCloudLogic.isCurrentUserPluginAdmin();
            }
        };
            

        List<ResourceToolAction> customActions = new ArrayList<ResourceToolAction>();
        customActions.add(configure);
        customActions.add(preview);
        
        actionMap.put(create.getActionType(), makeList(create));
        //actionMap.put(preview.getActionType(), makeList(preview));
        actionMap.put(preview.getActionType(), customActions);
        actionMap.put(remove.getActionType(), makeList(remove));
        
        actions.put(create.getId(), create);
        actions.put(configure.getId(), configure);
        actions.put(preview.getId(), preview);
        actions.put(remove.getId(), remove);
        
        resourceTypeRegistry.register(this);
	}
	
	public ResourceToolAction getAction(String actionId) {
		return actions.get(actionId);
	}

	public List<ResourceToolAction> getActions(ActionType type) {
	    List<ResourceToolAction> list = actionMap.get(type);
		if (list == null) {
			list = new Vector<ResourceToolAction>();
			actionMap.put(type, list);
		}
		return new Vector<ResourceToolAction>(list);
	}

	public List<ResourceToolAction> getActions(List<ActionType> types) {
	    List<ResourceToolAction> list = new Vector<ResourceToolAction>();
		if (types != null) {
			Iterator<ResourceToolAction.ActionType> it = types.iterator();
			while (it.hasNext()) {
				ResourceToolAction.ActionType type = it.next();
				List<ResourceToolAction> sublist = actionMap.get(type);
				if (sublist == null) {
					sublist = new Vector<ResourceToolAction>();
					actionMap.put(type, sublist);
				}
				list.addAll(sublist);
			}
		}
		return list;
	}

	public String getIconLocation(ContentEntity entity, boolean expanded)
    {
	    return getIconLocation(entity);
    }
	
	public String getIconLocation(ContentEntity entity) 
	{
	    return "../../scormcloud-tool/images/cloud_icon_sm.gif";
	}

	public String getId() {
		return SCORM_CONTENT_TYPE_ID;
	}

	public String getLabel() {
		return SCORM_CONTENT_LABEL;
	}

	public String getLocalizedHoverText(ContentEntity entity) {
		return SCORM_CONTENT_LABEL;
	}

	public String getLocalizedHoverText(ContentEntity entity, boolean expanded) {
		return "Scorm Cloud Content Package";
	}

    public String getLongSizeLabel(ContentEntity arg0) {
        return "";
    }

    public String getSizeLabel(ContentEntity arg0) {
        return "";
    }
}
