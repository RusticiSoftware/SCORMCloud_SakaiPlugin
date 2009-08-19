package org.sakaiproject.scormcloud.content.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.content.api.ResourceTypeRegistry;
import org.sakaiproject.content.api.ResourceToolAction.ActionType;
import org.sakaiproject.content.util.BaseInteractionAction;
import org.sakaiproject.user.api.UserDirectoryService;

public class ScormCloudPackageResourceType implements ResourceType {
    protected String typeId = SCORMCLOUD_TYPE;
    public static final String SCORMCLOUD_TYPE = "scormcloud.type";
    
    public static final String SCORM_CONTENT_LABEL="SCORM Cloud Package";
    public static final String SCORM_CONTENT_TYPE_ID="org.sakaiproject.content.types.scormCloudContentPackage";
    public static final String SCORM_UPLOAD_LABEL="Upload SCORM Cloud Package";
    public static final String SCORM_LAUNCH_LABEL="Launch";
    public static final String SCORM_REMOVE_LABEL="Remove";
    
    public static final String SCORM_ACCESS_HELPER_ID="sakai.scormcloud";
    public static final String SCORM_UPLOAD_HELPER_ID="sakai.scormcloud";
    
    List<String> requiredKeys = new ArrayList<String>();
    
    ResourceToolAction create = new BaseInteractionAction(ResourceToolAction.CREATE,
            ResourceToolAction.ActionType.NEW_UPLOAD,
            SCORM_CONTENT_TYPE_ID, 
            SCORM_UPLOAD_HELPER_ID, 
            requiredKeys) {
        public String getLabel() {
            return SCORM_UPLOAD_LABEL;
        }
    };
    
    ResourceToolAction launch = new BaseInteractionAction(ResourceToolAction.ACCESS_CONTENT, 
                                                          ResourceToolAction.ActionType.VIEW_CONTENT, 
                                                          SCORM_CONTENT_TYPE_ID, 
                                                          SCORM_ACCESS_HELPER_ID, 
                                                          requiredKeys) {
        public String getLabel() {
            return SCORM_LAUNCH_LABEL;
        }
    };

    private ResourceTypeRegistry resourceTypeRegistry;
    protected UserDirectoryService userDirectoryService;
    
    private HashMap<String, ResourceToolAction> actions = new HashMap<String, ResourceToolAction>();
    
    public void init() {
        this.typeId = "scormcloud.type";
        actions.put(ResourceToolAction.CREATE, create);
        actions.put(ResourceToolAction.ACCESS_CONTENT, launch);
        
        //if (ServerConfigurationService.getBoolean("enable.scorm", false)) {      
        //    getResourceTypeRegistry().register(this);
        // }
    }
    
    public ResourceTypeRegistry getResourceTypeRegistry() {
        //return resourceTypeRegistry;
        if (resourceTypeRegistry == null) {
            resourceTypeRegistry = (ResourceTypeRegistry) 
                ComponentManager.get("org.sakaiproject.content.api.ResourceTypeRegistry");
        }
        return resourceTypeRegistry;
     }

     public void setResourceTypeRegistry(ResourceTypeRegistry resourceTypeRegistry) {
        this.resourceTypeRegistry = resourceTypeRegistry;
     }

     public UserDirectoryService getUserDirectoryService() {
        return userDirectoryService;
     }

     public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
     }

    
    public ResourceToolAction getAction(String actionName) {
        return actions.get(actionName);
    }

    public List<ResourceToolAction> getActions(ActionType actionType) {
        ArrayList<ResourceToolAction> theseActions = new ArrayList<ResourceToolAction>();
        for (String actionName : actions.keySet()){
            ResourceToolAction act = actions.get(actionName);
            if(act != null && act.getActionType() == actionType){
                theseActions.add(act);
            }
        }
        return theseActions;
    }

    public List<ResourceToolAction> getActions(List<ActionType> actionTypes) {
        ArrayList<ResourceToolAction> theseActions = new ArrayList<ResourceToolAction>();
        for (String actionName : actions.keySet()){
            ResourceToolAction act = actions.get(actionName);
            for (ActionType actType : actionTypes){
                if(act != null && act.getActionType() == actType){
                    theseActions.add(act);
                }
            }
        }
        return theseActions;
    }

    public String getIconLocation(ContentEntity arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return typeId;
    }

    public String getLabel() {
        return "scormcloud_type";
    }

    public String getLocalizedHoverText(ContentEntity arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLongSizeLabel(ContentEntity arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSizeLabel(ContentEntity arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasAvailabilityDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasDescription() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasGroupsDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasNotificationDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasOptionalPropertiesDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasPublicDialog() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean hasRightsDialog() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isExpandable() {
        // TODO Auto-generated method stub
        return false;
    }

}
