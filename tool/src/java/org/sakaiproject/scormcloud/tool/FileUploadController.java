package org.sakaiproject.scormcloud.tool;


import org.sakaiproject.content.api.*;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.Entity;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.apache.commons.fileupload.FileItem;

import com.rusticisoftware.hostedengine.client.Configuration;
import com.rusticisoftware.hostedengine.client.ScormCloud;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploadController extends SimpleFormController {


    protected ModelAndView onSubmit(
        HttpServletRequest request,
        HttpServletResponse response,
        Object command,
        BindException errors) throws ServletException, Exception {

        FileItem file = (FileItem) request.getAttribute("file");
        
        //ScormCloud.setConfiguration(new Configuration("http://dev.cloud.scorm.com/EngineWebServices/", "dave", "y7bxwHapEWuOz3ODfZJ2je0DPJaFP8kHqDrQ4Bld"));
        File tempFile = File.createTempFile("sakai-scorm-cloud", "package-file");
        file.write(tempFile);
        
        //String id = UUID.randomUUID().toString();
        //ScormCloud.getCourseService().ImportCourse("sakai-" + id, tempFile.getAbsolutePath());
        
      //TODO validate is zip file, has imsmanifest.xml etc.

       ToolSession toolSession = SessionManager.getCurrentToolSession();
       ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
       ContentEntity contentEntity = pipe.getContentEntity();

      try {

          ContentResourceEdit  resource = getContentHostingService().addResource(contentEntity.getId(), file.getName(), "zip", ContentHostingService.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);
          ResourcePropertiesEdit properties = resource.getPropertiesEdit();
          properties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, file.getName());
          properties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
          properties.addProperty(org.sakaiproject.content.cover.ContentHostingService.PROP_ALTERNATE_REFERENCE, Entity.SEPARATOR + "scorm");
          resource.setContent(file.get());
          resource.setContentType("application/zip");
          resource.setResourceType("scorm.type");
          getContentHostingService().commitResource(resource);
//
          pipe.setActionCompleted(true);
          pipe.setActionCanceled(false);
//
//
//
             //getContentHostingService().setPubView(resource.getId(), pubview);
          }
          catch (Exception e) {
             throw new RuntimeException(e);
          }




        // leave helper mode
       pipe.setActionCanceled(false);
       pipe.setErrorEncountered(false);
       pipe.setActionCompleted(true);

       toolSession.setAttribute(ResourceToolAction.DONE, Boolean.TRUE);
       toolSession.removeAttribute(ResourceToolAction.STARTED);
       Tool tool = ToolManager.getCurrentTool();
       String url = (String) toolSession.getAttribute(tool.getId() + Tool.HELPER_DONE_URL);
       toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);

       try
       {
          response.sendRedirect(url);  // TODO
       }
       catch (IOException e)
       {
          logger.warn("IOException", e);
          // Log.warn("chef", this + " : ", e);
       }
       return null;
    }

   public ContentHostingService getContentHostingService() {
      return org.sakaiproject.content.cover.ContentHostingService.getInstance();
   }


}

