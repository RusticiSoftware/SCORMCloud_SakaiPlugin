package org.sakaiproject.scormcloud.content.types;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.tool.cover.SessionManager;

public class ScormCloudHttpAccess implements HttpAccess {
    public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {
        try {
            String packageId = ref.getId();
           //String uuid = ContentHostingService.getUuid(ref.getId());
           //String sessionId = SessionManager.getCurrentSession().getId();
           //String learnerId = SessionManager.getCurrentSession().getUserId();
           redirectToLaunchPage(req, res, packageId);
          // redirectAndLaunch(res, uuid, sessionId, learnerId);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
     }

     private void redirectToLaunchPage(HttpServletRequest req, HttpServletResponse res, String packageId) throws IOException {
        res.sendRedirect("/scormcloud-tool/controller?action=launchPackage&id=" + packageId);     
        //res.sendRedirect("http://www.google.com");
     }


     /*private void redirectAndLaunch(HttpServletResponse res, String uuid, String sessionId, String learnerId) throws IOException {
        res.sendRedirect(getLaunchUrl(uuid, sessionId, learnerId));
     }

     private String getLaunchUrl(String uuid, String sessionId, String learnerId) {
        return ServerConfigurationService.getServerUrl() +
              "/player2/skins/main.do?learnerID=" + learnerId + "&courseID=" + uuid +
              "&sessionID=" + sessionId + "&domainID=sakai";
     }*/

}
