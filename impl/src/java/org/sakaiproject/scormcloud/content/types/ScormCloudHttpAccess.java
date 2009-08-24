package org.sakaiproject.scormcloud.content.types;

import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;

public class ScormCloudHttpAccess implements HttpAccess {
    private static Log log = LogFactory.getLog(ScormCloudHttpAccess.class);
    
    public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
        try {
            String packageId = (String)ref.getEntity()
                                        .getProperties()
                                        .get(ScormCollectionType.PROP_SCORMCLOUD_PACKAGE_ID);
           
            String refId = ref.getId();
            
            //Parse out a possible assignment context of this reference
            if(refId.contains("Assignments")){
                //Typically, reference is like "Assignments/[assignment id/etc",
                //here we parse out the [assignment id]
                String assignmentString = refId.substring(refId.indexOf("Assignments"));
                int beginAssignmentId = assignmentString.indexOf("/") + 1;
                int endAssignmentId = assignmentString.indexOf("/", beginAssignmentId);
                String assignmentId = assignmentString.substring(beginAssignmentId, endAssignmentId);
                
                log.debug("In handleAccess: assignmentId = " + assignmentId);
            }
           
            redirectToLaunchPage(req, res, packageId, "");
           
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
     }

     private void redirectToLaunchPage(HttpServletRequest req, HttpServletResponse res, String packageId, String assignmentId) throws Exception {
        res.sendRedirect("/scormcloud-tool/controller?action=launchPackage&id=" + packageId); 
     }

}
