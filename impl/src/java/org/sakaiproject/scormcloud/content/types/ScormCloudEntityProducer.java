package org.sakaiproject.scormcloud.content.types;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.cover.EntityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScormCloudEntityProducer implements EntityProducer {
    protected final Log log = LogFactory.getLog(getClass());
    private boolean m_shortRefs = true;
    private boolean m_siteAlias = true;
    private static final String SCORM_LABEL = "scormcloud";
    public final static String REFERENCE_ROOT = Entity.SEPARATOR + SCORM_LABEL;
    private HttpAccess httpAccess;
    public void init() {
       log.info("init()");
       try {
          EntityManager.registerEntityProducer(this, Entity.SEPARATOR + SCORM_LABEL);
       } catch (Exception e) {
          log.warn("Error registering Scorm Entity Producer", e);
       }
    }

    public String getLabel() {
       return SCORM_LABEL;
    }

    public boolean willArchiveMerge() {
       return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments) {
       return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
       return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

     public boolean parseEntityReference(String reference, Reference ref)
     {
         String id = null;
         String context = "";
         
         log.debug("In parseEntityReference: reference = " + reference);

         // for content hosting resources and collections
         if (reference.startsWith(REFERENCE_ROOT))
         {
             // parse out the local resource id
             id = reference.replaceFirst(REFERENCE_ROOT + Entity.SEPARATOR + "content", "");
             
             
         }

         // not mine
         else
         {
             return false;
         }

         ref.set(REFERENCE_ROOT, null, id, null, context);

         // because short refs or id/eid or alias processing may recognize a reference that is not the real reference,
         // update the ref's string to reflect the real reference
         ref.updateReference(REFERENCE_ROOT + id);

         return true;
     }

    public String getEntityDescription(Reference ref) {
       return "Scorm Content";  
    }

    public ResourceProperties getEntityResourceProperties(Reference ref) {
       ResourceProperties rp = getEntity(ref).getProperties();
       rp.addProperty(org.sakaiproject.content.api.ContentHostingService.PROP_ALTERNATE_REFERENCE, REFERENCE_ROOT);
       return rp;
    }

    public Entity getEntity(Reference ref) {
       try {
          ContentResource rv = ContentHostingService.getResource(ref.getId());
          return new ScormCloudEntity(rv);
       } catch (Exception e) {
          throw new RuntimeException(e);
       }
    }

    public String getEntityUrl(Reference ref) {
       return ServerConfigurationService.getAccessUrl() + ref.getReference();
    }

    public Collection getEntityAuthzGroups(Reference ref, String userId) {
       return null;
    }

    public HttpAccess getHttpAccess() {
       return httpAccess;
    }

    public void setHttpAccess(HttpAccess httpAccess) {
       this.httpAccess = httpAccess;
    }

}
