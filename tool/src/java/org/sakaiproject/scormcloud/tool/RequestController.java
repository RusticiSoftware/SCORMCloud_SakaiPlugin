package org.sakaiproject.scormcloud.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
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
import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
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
	
	public static final String PAGE_PLUGIN_CONFIGURE = "ScormCloudConfiguration.jsp";
	public static final String PAGE_PACKAGE_IMPORT = "ImportPackage.jsp";
	public static final String PAGE_PACKAGE_LIST = "PackageList.jsp";
	public static final String PAGE_PACKAGE_EDIT = "EditPackage.jsp";
	public static final String PAGE_REGISTRATION_LIST = "RegistrationList.jsp";
	public static final String PAGE_REGISTRATION_LAUNCH = "Launch.jsp";
	public static final String PAGE_SHOW_MESSAGE = "ShowMessage.jsp";
	public static final String PAGE_WELCOME = "Welcome.jsp";
	public static final String PAGE_CLOSER = "Closer.html";
	
	private static final List<String> pagesAllowedByNonAdmin = 
	    Arrays.asList(new String[]{PAGE_WELCOME, PAGE_REGISTRATION_LAUNCH, PAGE_CLOSER});
	
	private static final List<String> actionsAllowedByNonAdmin =
	    Arrays.asList(new String[]{"launchPackage", "postLaunchActions", "closeWindow"});
	
	
	private ApplicationContext appContext;
	private ScormCloudLogic logic;
	private ExternalLogic extLogic;
	private ScormCloudPackagesBean pkgsBean;
	
	private void initInterfaces(){
	    if(appContext == null){
	        appContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	    }
	    if(logic == null){
	        logic = (ScormCloudLogic)appContext
	            .getBean("org.sakaiproject.scormcloud.logic.ScormCloudLogic");
	    }
	    if (extLogic == null){
	        extLogic = (ExternalLogic)appContext
	            .getBean("org.sakaiproject.scormcloud.logic.ExternalLogic");
	    }
	    if (pkgsBean == null){
	        pkgsBean = (ScormCloudPackagesBean)appContext
	            .getBean("packagesBean");
	    }
	}

	private void doSecurityFilterOnAction(HttpServletRequest request, 
	        HttpServletResponse response, String action) throws Exception {
	    boolean admin = logic.isCurrentUserPluginAdmin() || logic.isCurrentUserSakaiAdmin();
	    boolean actionAllowed = actionsAllowedByNonAdmin.contains(action);
	    if (!admin && !actionAllowed) {
            log.warn("Warning: security filter had to deny user " + 
                    extLogic.getUserDisplayId(extLogic.getCurrentUserId()) +
                    " from performing action: " + action);
            sendToMessagePage(request, response, 
                    "Action not allowed", 
                    "We're sorry, but the current settings for the " +
                    "SCORM Cloud plugin disallow you from performing " +
                    "this action. Please contact your local Sakai or SCORM Plugin " +
                    "administrator if you feel you've recieved this message in error.");
            return;
        }
	}
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try{
		    initInterfaces();
		    
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
            
            doSecurityFilterOnAction(request, response, action);
            
            if(action.equals("viewPackages")){
                processViewPackagesRequest(request, response);
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

    private void processViewPackagesRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<ScormCloudPackage> pkgList = logic.getAllSitePackages();
        request.setAttribute("pkgList", pkgList);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_PACKAGE_LIST);
        rd.forward(request, response);
    }

    private void sendToMessagePage(HttpServletRequest request, HttpServletResponse response, 
            String messageTitle, String message) throws Exception {
	    sendToMessagePage(request, response, messageTitle, message, false);
	}
	
	private void sendToMessagePage(HttpServletRequest request, HttpServletResponse response, 
	        String messageTitle, String message, boolean hideBackLink) throws Exception {
        request.setAttribute("messageTitle", messageTitle);
        request.setAttribute("message", message);
        if(hideBackLink){
            request.setAttribute("hideBackLink", true);
        }
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_SHOW_MESSAGE);
        rd.forward(request, response);
    }

    private void proccessViewCloudConfigurationAction(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    ScormCloudConfiguration config = logic.getScormCloudConfiguration();
        if (config == null) {
            config = new ScormCloudConfiguration();
        }
        request.setAttribute("config", config);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_PLUGIN_CONFIGURE);
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
            logic.setScormCloudConfiguration(config);
	    }
	    RequestDispatcher rd = request.getRequestDispatcher(PAGE_WELCOME);
	    rd.forward(request, response);
    }

    private void processViewPackagePropertiesRequest(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        ScormCloudPackage pkg = logic.getPackageById(packageId);
        String packagePropertiesUrl = logic.getPackagePropertiesUrl(pkg);
        request.setAttribute("pkg", pkg);
        request.setAttribute("packagePropertiesUrl", packagePropertiesUrl);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_PACKAGE_EDIT);
        rd.forward(request, response);
    }

    private void processPostLaunchActions(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String regId = request.getParameter("regId");
        log.debug("processPostLaunchActions called with regId = " + regId);
        ScormCloudRegistration reg = logic.getRegistrationById(regId);
        if (reg != null){
            logic.updateRegistrationResultsFromCloud(reg);  
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
                logic.getPackageById(packageId));
        
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
                logic
                    .getRegistrationsByPackageId(packageId));
        
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_REGISTRATION_LIST);
        rd.forward(request, response);
	}

    private void processUpdateRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsUpdated = 0;
            for (String id : selectedItems){
                ScormCloudRegistration reg = logic.getRegistrationById(id);
                logic.updateRegistrationResultsFromCloud(reg);
            }
            pkgsBean.getMessages().add("Updated " + itemsUpdated + " items");
        }
    }

    private void processDeleteRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudPackagesBean bean = pkgsBean;
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsRemoved = 0;
            for (String id : selectedItems){
                ScormCloudRegistration reg = logic.getRegistrationById(id);
                if(reg != null){
                    logic.removeRegistration(reg);
                    itemsRemoved++;
                }
            }
            bean.messages.add("Removed " + itemsRemoved + " items");
        }
    }
    
    private void processResetRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudPackagesBean bean = pkgsBean;
        String[] selectedItems = request.getParameterValues("select-item");
        if (selectedItems != null && selectedItems.length > 0) {
            int itemsReset = 0;
            for (String id : selectedItems){
                ScormCloudRegistration reg = logic.getRegistrationById(id);
                if(reg != null){
                    logic.resetRegistration(reg);
                    itemsReset++;
                }
            }
            bean.messages.add("Reset " + itemsReset + " registrations");
        }
    }

    private void processViewRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        String packageId = request.getParameter("packageId");
        String userSearch = request.getParameter("userSearch");
        String assignmentSearch = request.getParameter("assignmentSearch");
        
        if(!isNullOrEmpty(packageId)){
            request.setAttribute("pkg", logic.getPackageById(packageId));
        }
        
        HashMap<String, Object> propertyMap = new HashMap<String, Object>();
        if (!isNullOrEmpty(packageId)) 
            propertyMap.put("packageId", packageId);
        if (!isNullOrEmpty(userSearch)) 
            propertyMap.put("userName", userSearch);
        if (!isNullOrEmpty(assignmentSearch)) 
            propertyMap.put("assignmentName", assignmentSearch);
        
        List<ScormCloudRegistration> regList = logic.getRegistrationsByPropertyMap(propertyMap);
        
        if(regList != null){
            request.setAttribute("regList", regList);
        }
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_REGISTRATION_LIST);
        rd.forward(request, response);
    }

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

	private void processDeletePackageResourceRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	       ToolSession toolSession = SessionManager.getCurrentToolSession();
	       ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
	       ContentEntity contentEntity = pipe.getContentEntity();

	       try {
	           String packageId = (String)contentEntity.getProperties().get(PROP_SCORMCLOUD_PACKAGE_ID);
	           ScormCloudPackage pkg = logic.getPackageById(packageId);
	           getContentHostingService().removeResource(contentEntity.getId());
	           logic.removePackage(pkg);
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
        pkg.setTitle(params.get("package-title"));
		
		//Does the package contribute to associated assignments?
		String contributes = params.get("contribute-to-assigment-grade");
		pkg.setContributesToAssignmentGrade(Boolean.parseBoolean(contributes));
		
		//Allow the package to be launched outside of an assignment?
		String allowNonAssignmentLaunch = params.get("allow-non-assignment-launch");
		pkg.setAllowLaunchOutsideAssignment(Boolean.parseBoolean(allowNonAssignmentLaunch));
		
		//Create new cloud id for package
		String cloudId = UUID.randomUUID().toString();
		pkg.setScormCloudId("sakai-" + cloudId);
		
		//Add package through packages bean
        logic.addNewPackage(pkg, tempFile);     
        
        //Clean up the temp file now that we're done
        tempFile.delete();

        String helper = params.get("helper");
        log.debug("helper = " + helper);
        if ("true".equals(helper)) {
            log.debug("Helper mode for import, creating resource type");
            processImportHelperActions(pkg, request, response);
        }
        //Send the user back to the package list page
		response.sendRedirect(PAGE_PACKAGE_LIST);
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
		String assignmentKey = request.getParameter("assignmentKey");
		String resourceLink = request.getParameter("resourceLink");
		
		log.debug("action launchPackage requested with packageId = " + packageId +
		          " assignmentKey = " + assignmentKey);

		//Go get the package specified by the id in the request
		ScormCloudPackage pkg = logic.getPackageById(packageId);
		
		log.debug("isNullOrEmpty(assignmentKey)? " + isNullOrEmpty(assignmentKey));
		log.debug("assignmentKey == null? " + (assignmentKey == null));
		log.debug("assignmentKey.length()? " + assignmentKey.length());
		log.debug("getAllowLaunchOutsideAssignment? " + pkg.getAllowLaunchOutsideAssignment());
		log.debug("!getAllowLaunchOutsideAssignment? " + !pkg.getAllowLaunchOutsideAssignment());
		
		//If no assignment context, make sure we can still launch...
        if(isNullOrEmpty(assignmentKey) && !pkg.getAllowLaunchOutsideAssignment()){
            sendToMessagePage(request, response, 
                "Launch Not Allowed", 
                "We're sorry, but current settings for this SCORM Cloud " +
                "resource disallow you from launching it outside of " +
                "the context of an assignment.");
            return;
        }
		
		//Find (or create) a registration for the current user and the given package
		String userId = extLogic.getCurrentUserId();
		ScormCloudRegistration reg = logic.findRegistrationFor(pkg, userId, assignmentKey);
        if(reg == null){
            reg = logic.addNewRegistration(pkg, userId, assignmentKey);
        }

        //Now get the launch url associated with the registration...
		String launchUrl = logic.getLaunchUrl(reg, 
		        getAbsoluteUrlToSelf(request) + "?action=postLaunchActions&regId=" + reg.getId());
		
		//Forward the user to the launch page, now that a registration exists for them
		log.debug("launchUrl = " + launchUrl);
		request.setAttribute("url", launchUrl);
		
		//If via resource link, include that in page request
		if(!isNullOrEmpty(resourceLink)){
		    request.setAttribute("resourceLink", true);
		}
		
		RequestDispatcher rd = request.getRequestDispatcher(PAGE_REGISTRATION_LAUNCH);
		rd.forward(request, response);
	}
	
	/**
     * Create or find a registration for the current user and the specified package,
     * and redirect the user to the launch page
     */
    public void processPreviewRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        log.debug("action prewiewPackage requested with packageId = " + packageId);
        
        //Go get the package specified by the id in the request
        ScormCloudPackage pkg = logic.getPackageById(packageId);
        if(pkg == null){
            log.debug("Error in launchPackage action, no package with id = " + packageId + " found!");
            request.setAttribute("errorMessage", "Package with id " + packageId + " not found!");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }
        
        String previewUrl = logic.getPackagePreviewUrl(pkg, 
                                getAbsoluteUrlToSelf(request) + "?action=closeWindow");
        
        //Forward the user to the launch page
        log.debug("previewUrl = " + previewUrl);
        request.setAttribute("url", previewUrl);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_REGISTRATION_LAUNCH);
        rd.forward(request, response);
    }

	/**
	 * Get the scorm cloud packages bean
	 */
	
	
	private String getAbsoluteUrlToSelf(HttpServletRequest request) throws Exception {
	    
	    URL controllerUrl = new URL(request.getScheme(),
	                                   request.getServerName(),
	                                   request.getServerPort(),
	                                   request.getRequestURI());
	    return controllerUrl.toString();
	}
	
	private boolean isNullOrEmpty(String str){
	    return (str == null || str.length() == 0);
	}
}
