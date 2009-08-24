package org.sakaiproject.scormcloud.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.content.api.ResourceToolActionPipe;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class RequestController extends HttpServlet {
    private static final String PROP_SCORMCLOUD_PACKAGE_ID = "packageId";
    private static Log log = LogFactory.getLog(RequestController.class);
	private static final long serialVersionUID = 1L;

	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try{
		    log.debug("paramaterMap size = " + request.getParameterMap().size());
			PrintWriter output = response.getWriter();
			String action = request.getParameter("action");
			
			if(action == null || action.length() < 1){
				log.debug("No action specified. Looking for one in servlet init params");
				try { 
				    action = getServletConfig().getInitParameter("action"); 
				    log.debug("Found action " + action + " in init params");
				}
				catch (Exception e) {
				    log.debug("Exception looking for action in init params", e);
				}
			}
			
            if(action == null || action.length() < 1){
                log.error("No action specified, returning!");
                return;
            }
            if(action.equals("viewCloudConfiguration")){
                proccessViewCloudConfigurationAction(request, response);
            }
			if(action.equals("configureCloudPlugin")){
			    processConfigureCloudPluginAction(request, response);
			}
			if(action.equals("importPackage")){
				processImportRequest(request, response);
			}
			if(action.equals("deletePackageResource")){
			    processDeletePackageResourceRequest(request, response);
			}
			if(action.equals("previewPackage")){
			    processPreviewRequest(request, response);
			}
			if(action.equals("launchPackage")){
				processLaunchRequest(request, response);
			}
			if(action.equals("processPackageListAction")){
			    if (request.getParameterValues("delete-items") != null) {
			        processDeletePackagesRequest(request, response);
			    }
			}
			if(action.equals("processRegistrationListAction")){
			    processRegistrationListRequest(request, response);
			}
			if(action.equals("viewRegistrations")){
			    processViewRegistrationsRequest(request, response);
			}
			if(action.equals("postLaunchActions")){
			    processPostLaunchActions(request, response);
			}
			if(action.equals("updatePackage")){
				/* Not implemented yet */
			}
			if(action.equals("updateRegSummaryData")){
				/* Not implemented yet */
			}
			if(action.equals("viewPackageProperties")){
				processViewPackagePropertiesRequest(request, response);
			}
			if(action.equals("closeWindow")){
			    response.sendRedirect("Closer.html");
			}
			if(action.equals("debugParams")){
			    log.debug("Debugging params sent");
			    Map params = request.getParameterMap();
			    for (Object key : params.keySet()){
			        log.debug((String)key + " = " + (String)request.getParameter((String)key));
			    }
			}
			
			output.println("error: Action " + action + " not found");
		}
		catch (Exception e){
			throw new ServletException(e);
		}
	}
	
	private void proccessViewCloudConfigurationAction(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    ScormCloudConfiguration config = getScormCloudPackagesBean().getConfiguration();
        if (config == null) {
            config = new ScormCloudConfiguration();
        }
        request.setAttribute("config", config);
        RequestDispatcher rd = request.getRequestDispatcher("ScormCloudConfiguration.jsp");
        rd.forward(request, response);
	}
	
	private void processConfigureCloudPluginAction(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
	    if(request.getParameter("submit") != null){
            String appId = request.getParameter("appId");
            String secretKey = request.getParameter("secretKey");
            String serviceUrl = request.getParameter("serviceUrl");
            
            ScormCloudConfiguration config = new ScormCloudConfiguration();
            config.setAppId(appId);
            config.setSecretKey(secretKey);
            config.setServiceUrl(serviceUrl);
            getScormCloudPackagesBean().setConfiguration(config);
            
            getScormCloudPackagesBean().getMessages().add("SCORM Cloud Plugin Configuration Updated");
	    }
	    RequestDispatcher rd = request.getRequestDispatcher("PackageList.jsp");
	    rd.forward(request, response);
    }

    private void processViewPackagePropertiesRequest(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        ScormCloudPackage pkg = getScormCloudPackagesBean().getPackageById(packageId);
        String packagePropertiesUrl = getScormCloudPackagesBean().getPackagePropertiesUrl(pkg);
        request.setAttribute("pkg", pkg);
        request.setAttribute("packagePropertiesUrl", packagePropertiesUrl);
        RequestDispatcher rd = request.getRequestDispatcher("EditPackage.jsp");
        rd.forward(request, response);
    }

    private void processPostLaunchActions(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String regId = request.getParameter("regId");
        log.debug("processPostLaunchActions called with regId = " + regId);
        ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        ScormCloudRegistration reg = bean.getRegistrationById(regId);
        if (reg != null){
            bean.updateRegistrationResultsFromCloud(reg);  
        } else {
            log.debug("Error! Registration with id " + regId + "not found!");
        }
        response.sendRedirect("Closer.html");
    }

    private void processRegistrationListRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
	  //Make sure to recreate the "state" of the reg list page
        String packageId = request.getParameter("packageId");
        
        request.setAttribute("pkg", 
                getScormCloudPackagesBean()
                    .getPackageById(packageId));
        
        //Delete or update, based on button pressed
        if(request.getParameter("delete-items") != null){
            processDeleteRegistrationsRequest(request, response);
        }
        else if (request.getParameter("reset-items") != null){
            processResetRegistrationsRequest(request, response);
        }
        else if (request.getParameter("update-items") != null){
            processUpdateRegistrationsRequest(request, response);
        }
        
        //Now grab the altered registration list
        request.setAttribute("regList",
                getScormCloudPackagesBean()
                    .getRegistrationsByPackageId(packageId));
        
        RequestDispatcher rd = request.getRequestDispatcher("RegistrationList.jsp");
        rd.forward(request, response);
	}

    private void processUpdateRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsUpdated = 0;
            for (String id : selectedItems){
                ScormCloudRegistration reg = bean.getRegistrationById(id);
                bean.updateRegistrationResultsFromCloud(reg);
            }
            bean.messages.add("Updated " + itemsUpdated + " items");
        }
    }

    private void processDeleteRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsRemoved = 0;
            for (String id : selectedItems){
                if (bean.checkRemoveRegistrationById(id)) {
                    itemsRemoved++;
                } else {
                    bean.messages.add("Removal error: Cannot remove item with id: " + id);
                }
            }
            bean.messages.add("Removed " + itemsRemoved + " items");
        }
    }
    
    private void processResetRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsReset = 0;
            for (String id : selectedItems){
                if (bean.checkResetRegistrationById(id)) {
                    itemsReset++;
                } else {
                    bean.messages.add("Removal error: Cannot remove item with id: " + id);
                }
            }
            bean.messages.add("Reset " + itemsReset + " registrations");
        }
    }

    private void processViewRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        ScormCloudPackage pkg = (ScormCloudPackage)bean.getPackageById(packageId);
        if(pkg == null){
            log.debug("Error processing view registration request, " +
                      "package with id = " + packageId + " not found!");
            response.sendRedirect("PackageList.jsp");
        }
        List<ScormCloudRegistration> regList = bean.getRegistrationsByPackageId(pkg.getId());
        
        request.setAttribute("pkg", pkg);
        request.setAttribute("regList", regList);
        RequestDispatcher rd = request.getRequestDispatcher("RegistrationList.jsp");
        rd.forward(request, response);
    }

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

	public void processDeletePackagesRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    ScormCloudPackagesBean bean = getScormCloudPackagesBean();
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsRemoved = 0;
            for (int i=0; i<selectedItems.length; i++) {
                String id = selectedItems[i];
                if (bean.checkRemovePackageById(id)) {
                    itemsRemoved++;
                } else {
                    bean.messages.add("Removal error: Cannot remove item with id: " + id);
                }
            }
            bean.messages.add("Removed " + itemsRemoved + " items");
        }
	    response.sendRedirect("PackageList.jsp");
    }

	private void processDeletePackageResourceRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	       ToolSession toolSession = SessionManager.getCurrentToolSession();
	       ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
	       ContentEntity contentEntity = pipe.getContentEntity();

	       try {
	           ScormCloudPackagesBean bean = getScormCloudPackagesBean();
	           String packageId = (String)contentEntity.getProperties().get(PROP_SCORMCLOUD_PACKAGE_ID);
	           ScormCloudPackage pkg = bean.getPackageById(packageId);
	           if(bean.canDelete(pkg)){
    	           getContentHostingService().removeResource(contentEntity.getId());
    	           getScormCloudPackagesBean().checkRemovePackageById(packageId);
	           }
	       }
	       catch (Exception e) {
	           throw new RuntimeException(e);
	       }
	       
	       endToolHelperSession(toolSession, pipe, response);
	}
	
	/**
	 * Create a new package record and import posted file to the cloud.
	 */
	public void processImportRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//These params will be set to the request params after we parse the file upload request
		HashMap<String, String> params = new HashMap<String, String>();
		
		//Write upload data to temp file
		File tempFile = File.createTempFile("sakai-scorm-cloud-", ".zip");
		FileUploadUtils.parseFileUploadRequest(request, tempFile, params);
		
		//Create new package object
        ScormCloudPackage pkg = new ScormCloudPackage();
        pkg.setTitle(params.get("item-title"));
        Boolean itemHidden = Boolean.FALSE;
		if ( (params.get("item-hidden") != null) ) {
			itemHidden = Boolean.TRUE;
		}
		pkg.setHidden(itemHidden);
		
		//Create new cloud id for package
		String cloudId = UUID.randomUUID().toString();
		pkg.setScormCloudId("sakai-" + cloudId);
		
		//Add package through packages bean
        getScormCloudPackagesBean().addNewPackage(pkg, tempFile);     
        
        //Clean up the temp file now that we're done
        tempFile.delete();

        String helper = params.get("helper");
        log.debug("helper = " + helper);
        if ("true".equals(helper)) {
            log.debug("Helper mode for import, creating resource type");
            processImportHelperActions(pkg, request, response);
        }
        //Send the user back to the package list page
		response.sendRedirect("PackageList.jsp");
	}
	
	
	
	private void addResourceForScormCloudEntity(ContentEntity contentEntity, ScormCloudPackage pkg) throws Exception {
	    ContentResourceEdit  resource = getContentHostingService().addResource(contentEntity.getId(), pkg.getTitle(), "scormcloud", ContentHostingService.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);
        ResourcePropertiesEdit properties = resource.getPropertiesEdit();
        properties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, pkg.getTitle());
        properties.addProperty(org.sakaiproject.content.api.ContentHostingService.PROP_ALTERNATE_REFERENCE, Entity.SEPARATOR + "scormcloud");
        properties.addProperty(PROP_SCORMCLOUD_PACKAGE_ID, pkg.getId());
        resource.setContent(new byte[]{0,0,0,0});  //Dummy content, we don't need it...
        resource.setContentType("application/zip");
        resource.setResourceType("scormcloud.type");
        //resource.setHidden();
        getContentHostingService().commitResource(resource);
	}
	
	private void endToolHelperSession(ToolSession toolSession, ResourceToolActionPipe pipe, HttpServletResponse response) {
	    // Set the action completed in the action pipe
        pipe.setActionCanceled(false);
        pipe.setErrorEncountered(false);
        pipe.setActionCompleted(true);

        //Set the session to done
        toolSession.setAttribute(ResourceToolAction.DONE, Boolean.TRUE);
        toolSession.removeAttribute(ResourceToolAction.STARTED);
        
        //Redirect to the "done" url ourselves
        Tool tool = ToolManager.getCurrentTool();
        String url = (String) toolSession.getAttribute(tool.getId() + Tool.HELPER_DONE_URL);
        toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
        
        try {
           response.sendRedirect(url);
        }
        catch (IOException e) {
           log.warn("IOException", e);
        }
        
	}
	
	private void processImportHelperActions(ScormCloudPackage pkg, HttpServletRequest request, HttpServletResponse response) throws Exception {
       ToolSession toolSession = SessionManager.getCurrentToolSession();
       ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
       ContentEntity contentEntity = pipe.getContentEntity();

       try {
          //Add the resource
           addResourceForScormCloudEntity(contentEntity, pkg);
       }
       catch (Exception e) {
           throw new RuntimeException(e);
       }
       
       endToolHelperSession(toolSession, pipe, response);
	}
	
	
	public ContentHostingService getContentHostingService() {
      return org.sakaiproject.content.cover.ContentHostingService.getInstance();
    }
	
	/**
	 * Create or find a registration for the current user and the specified package,
	 * and redirect the user to the launch page
	 */
	public void processLaunchRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String packageId = request.getParameter("id");
		log.debug("action launchPackage requested with packageId = " + packageId);
		
		//Grab the packages bean
		ScormCloudPackagesBean pkgsBean = getScormCloudPackagesBean();
		
		//Go get the package specified by the id in the request
		ScormCloudPackage pkg = pkgsBean.getPackageById(packageId);
		if(pkg == null){
			log.debug("Error in launchPackage action, no package with id = " + packageId + " found!");
			request.setAttribute("errorMessage", "Package with id " + packageId + " not found!");
			RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
		}
		
		//Find (or create) a registration for the current user and the given package
		ScormCloudRegistration reg = pkgsBean.findOrCreateUserRegistrationFor(pkg);

		//Here we check to see if the newly created reg was created outside of any
		//tool context (and hence unavailable to the grade book). If so, get the context
		//from the package
		if(reg.getContext() == null){
		    log.debug("New reg had a null context, setting context from package");
		    reg.setContext(pkg.getContext());
		    reg.setLocationId(pkg.getLocationId());
		    pkgsBean.updateRegistration(reg);
		}
		
		
		String launchUrl = pkgsBean.getLaunchUrl(reg, 
                                		        getAbsoluteUrlToSelf(request) + 
                                		            "?action=postLaunchActions&regId=" + 
                                		            reg.getId());
		
		//Forward the user to the launch page, now that a registration exists for them
		log.debug("launchUrl = " + launchUrl);
		request.setAttribute("url", launchUrl);
		RequestDispatcher rd = request.getRequestDispatcher("Launch.jsp");
		rd.forward(request, response);
	}
	
	/**
     * Create or find a registration for the current user and the specified package,
     * and redirect the user to the launch page
     */
    public void processPreviewRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        log.debug("action prewiewPackage requested with packageId = " + packageId);
        
        //Grab the packages bean
        ScormCloudPackagesBean pkgsBean = getScormCloudPackagesBean();
        
        //Go get the package specified by the id in the request
        ScormCloudPackage pkg = pkgsBean.getPackageById(packageId);
        if(pkg == null){
            log.debug("Error in launchPackage action, no package with id = " + packageId + " found!");
            request.setAttribute("errorMessage", "Package with id " + packageId + " not found!");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }
        
        String previewUrl = pkgsBean.getPackagePreviewUrl(pkg, 
                                getAbsoluteUrlToSelf(request) + "?action=closeWindow");
        
        //Forward the user to the launch page
        log.debug("previewUrl = " + previewUrl);
        request.setAttribute("url", previewUrl);
        RequestDispatcher rd = request.getRequestDispatcher("Launch.jsp");
        rd.forward(request, response);
    }

	/**
	 * Get the scorm cloud packages bean
	 */
	private ScormCloudPackagesBean getScormCloudPackagesBean()
	{
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return (ScormCloudPackagesBean)context.getBean("packagesBean");
	}
	
	private String getAbsoluteUrlToSelf(HttpServletRequest request) throws Exception {
	    
	    URL controllerUrl = new URL(request.getScheme(),
	                                   request.getServerName(),
	                                   request.getServerPort(),
	                                   request.getRequestURI());
	    return controllerUrl.toString();
	}
	
}
