/*
 *   Copyright 2009-2010 Rustici Software. Licensed under the
 *   Educational Community License, Version 2.0 (the "License"); you may
 *   not use this file except in compliance with the License. You may
 *   obtain a copy of the License at
 *   
 *   http://www.osedu.org/licenses/ECL-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */


package com.rusticisoftware.scormcloud.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
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
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import com.rusticisoftware.scormcloud.logic.ExternalLogic;
import com.rusticisoftware.scormcloud.logic.ScormCloudLogic;
import com.rusticisoftware.scormcloud.model.ScormCloudConfiguration;
import com.rusticisoftware.scormcloud.model.ScormCloudPackage;
import com.rusticisoftware.scormcloud.model.ScormCloudRegistration;



public class RequestController extends HttpServlet {
    private static final String PROP_SCORMCLOUD_PACKAGE_ID = "packageId";
    private static Log log = LogFactory.getLog(RequestController.class);
	private static final long serialVersionUID = 1L;
	
	private static final String URL_EMBEDDED_SIGNUP = "https://accounts.scorm.com/scorm-cloud-manager/public/signup-embedded";
	private static final String URL_USAGE_METER = "https://accounts.scorm.com/scorm-cloud-manager/public/usage-meter";
	
	public static final String PAGE_PLUGIN_CONFIGURE = "ScormCloudConfiguration.jsp";
	public static final String PAGE_PACKAGE_IMPORT = "ImportPackage.jsp";
	public static final String PAGE_PACKAGE_LIST = "PackageList.jsp";
	public static final String PAGE_PACKAGE_EDIT = "EditPackage.jsp";
	public static final String PAGE_REGISTRATION_LIST = "RegistrationList.jsp";
	public static final String PAGE_REGISTRATION_LAUNCH = "Launch.jsp";
	public static final String PAGE_SHOW_MESSAGE = "ShowMessage.jsp";
	public static final String PAGE_WELCOME = "Welcome.jsp";
	public static final String PAGE_CLOSER = "Closer.html";
	private static final String PAGE_ACTIVITY_REPORT = "ActivityReport.jsp";
	private static final String PAGE_LAUNCH_HISTORY_REPORT = "LaunchHistory.jsp";
	private static final String PAGE_SIGNUP = "Signup.jsp";
	private static final String PAGE_USAGE = "Usage.jsp";
	private static final String PAGE_REPORTAGE_OVERALL_REPORT = "ReportageOverallReport.jsp";
	private static final String PAGE_REPORTAGE_COURSE_REPORT = "ReportageCourseReport.jsp";
	private static final String PAGE_REPORTAGE_LEARNER_REPORT = "ReportageLearnerReport.jsp";
	
	private static final List<String> pagesAllowedByNonAdmin = 
	    Arrays.asList(new String[]{PAGE_WELCOME, PAGE_REGISTRATION_LAUNCH, PAGE_CLOSER});
	
	private static final List<String> actionsAllowedByNonAdmin =
	    Arrays.asList(new String[]{"launchPackage", "postLaunchActions", "closeWindow"});
    
    
	
	
	private ApplicationContext appContext;
	private ScormCloudLogic logic;
	private ExternalLogic extLogic;
	private ScormCloudToolBean toolBean;
	
	private void initInterfaces(){
	    if(appContext == null){
	        appContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	    }
	    if(logic == null){
	        logic = (ScormCloudLogic)appContext
	            .getBean("com.rusticisoftware.scormcloud.logic.ScormCloudLogic");
	    }
	    if (extLogic == null){
	        extLogic = (ExternalLogic)appContext
	            .getBean("com.rusticisoftware.scormcloud.logic.ExternalLogic");
	    }
	    if (toolBean == null){
	        toolBean = (ScormCloudToolBean)appContext
	            .getBean("scormCloudToolBean");
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
            else if(action.equals("viewCloudConfiguration")){
                proccessViewCloudConfigurationAction(request, response);
            }
            else if(action.equals("configureCloudPlugin")){
			    processConfigureCloudPluginAction(request, response);
			}
            else if(action.equals("importPackage")){
				processImportRequest(request, response);
			}
            else if(action.equals("configurePackageResource")){
                processConfigurePackageResourceRequest(request, response);
            }
            else if(action.equals("previewPackageResource")){
                processPreviewPackageResourceRequest(request, response);
            }
            else if(action.equals("deletePackageResource")){
			    processDeletePackageResourceRequest(request, response);
			}
            else if(action.equals("previewPackage")){
			    processPreviewRequest(request, response);
			}
            else if(action.equals("launchPackage")){
				processLaunchRequest(request, response);
			}
            else if(action.equals("processRegistrationListAction")){
			    processRegistrationListRequest(request, response);
			}
            else if(action.equals("viewRegistrations")){
			    processViewRegistrationsRequest(request, response);
			}
            else if(action.equals("viewActivityReport")){
			    processViewActivityReportRequest(request, response);
			}
            else if(action.equals("viewLaunchHistoryReport")){
			    processViewLaunchHistoryReport(request, response);
			}
            else if(action.equals("getLaunchInfoXml")){
			    output.print(getLaunchHistoryInfoXml(request, response));
			    return;
			}
            else if(action.equals("postLaunchActions")){
			    processPostLaunchActions(request, response);
			}
            else if(action.equals("viewPackageProperties")){
				processViewPackagePropertiesRequest(request, response);
			}
            else if(action.equals("viewOverallGroupReport")){
                processViewOverallGroupReportRequest(request, response);
            }
            else if(action.equals("viewCourseReport")){
                processViewCourseReportRequest(request, response);
            }
            else if(action.equals("launchReportage")){
                processLaunchReportageRequest(request, response);
            }
            else if(action.equals("closeWindow")){
			    response.sendRedirect("Closer.html");
			}
            else if(action.equals("viewSignup")){
                processViewSignupRequest(request, response);
            }
            else if(action.equals("viewUsage")){
                processViewUsageRequest(request, response);
            }
            else if(action.equals("debugParams")){
			    log.debug("Debugging params sent");
			    Map params = request.getParameterMap();
			    for (Object key : params.keySet()){
			        log.debug((String)key + " = " + (String)request.getParameter((String)key));
			    }
			}
            else {
                output.println("error: Action " + action + " not found");
            }
		}
		catch (Exception e){
			throw new ServletException(e);
		}
	}

    private void processLaunchReportageRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String appId = logic.getScormCloudConfiguration().getAppId();
        String reportageAuth = logic.getReportageAuth("FREENAV", true);
        String reportageHome = "/Reportage/reportage.php?appId=" + appId;
        response.sendRedirect(logic.getReportUrl(reportageAuth, reportageHome));
    }

	private void processViewOverallGroupReportRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String learnerTags = request.getParameter("learnerTags");
        String courseTags = request.getParameter("courseTags");
        
        boolean learnerTagsPresent = !isNullOrEmpty(learnerTags);
        boolean courseTagsPresent = !isNullOrEmpty(courseTags);
        boolean tagsPresent = learnerTagsPresent || courseTagsPresent;
        
        //TODO: Use the tag parameters, but first we need a place where the
        //tags are used automatically or manually in sakai
	    
	    String appId = logic.getScormCloudConfiguration().getAppId();
        String reportageAuth = logic.getReportageAuth("FREENAV", true);
        
        String summaryUrl = "/Reportage/scormreports/widgets/summary/SummaryWidget.php?appId=" + appId + "&srt=allLearnersAllCourses" +
                "&standalone=true&embedded=true&expand=true&showTitle=true" +
                "&scriptBased=true&divname=reportageSummary";
        
        
        String learnerDetailsUrl = "/Reportage/scormreports/widgets/DetailsWidget.php?appId=" + appId + "&drt=learnerRegistration" +
                "&standalone=true&embedded=true&showTitle=true&expand=true" +
                "&scriptBased=true&divname=reportageLearnerDetails";
        
        
        String courseDetailsUrl = "/Reportage/scormreports/widgets/DetailsWidget.php?appId=" + appId + "&drt=courseRegistration" +
                "&standalone=true&embedded=true&showTitle=true&expand=true" +
                "&scriptBased=true&divname=reportageCourseDetails";

        
        request.setAttribute("summaryUrl", logic.getReportUrl(reportageAuth, summaryUrl));
        request.setAttribute("learnerDetailsUrl", logic.getReportUrl(reportageAuth, learnerDetailsUrl));
        request.setAttribute("courseDetailsUrl", logic.getReportUrl(reportageAuth, courseDetailsUrl));
        
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_REPORTAGE_OVERALL_REPORT);
        rd.forward(request, response);
    }
	
	private void processViewCourseReportRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String packageId = request.getParameter("packageId");
	    ScormCloudPackage pkg = logic.getPackageById(packageId);
        
	    String learnerTags = request.getParameter("learnerTags");
        String courseTags = request.getParameter("courseTags");
        
        boolean learnerTagsPresent = !isNullOrEmpty(learnerTags);
        boolean courseTagsPresent = !isNullOrEmpty(courseTags);
        boolean tagsPresent = learnerTagsPresent || courseTagsPresent;
        
        String appId = logic.getScormCloudConfiguration().getAppId();
        String reportageAuth = logic.getReportageAuth("FREENAV", true);
        
        String courseSummaryUrl = "/Reportage/scormreports/widgets/summary/SummaryWidget.php?appId=" + appId + "&srt=singleCourse" +
                "&courseId=" + URLEncoder.encode(pkg.getScormCloudId(), "UTF-8") +
                "&standalone=true&embedded=true&expand=true&showTitle=true" +
                "&scriptBased=true&divname=courseSummary";
        
        request.setAttribute("summaryUrl", logic.getReportUrl(reportageAuth, courseSummaryUrl));
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_REPORTAGE_COURSE_REPORT);
        rd.forward(request, response);
	}

    private void processViewUsageRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String params = 
            getEncodedParam("appId", logic.getScormCloudConfiguration().getAppId()) + "&" +
            getEncodedParam("cssurl", getAbsoluteUrlTo(request, "/scormcloud-tool/css/SignupAndUsage.css"));

        request.setAttribute("usageUrl", URL_USAGE_METER + "?" + params);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_USAGE);
        rd.forward(request, response);
    }

    private void processViewSignupRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String params =
            getEncodedParam("cssurl", getAbsoluteUrlTo(request, "/scormcloud-tool/css/SignupAndUsage.css"));
        
        request.setAttribute("signupUrl", URL_EMBEDDED_SIGNUP + "?" + params);
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_SIGNUP);
        rd.forward(request, response);
    }

    private void processConfigurePackageResourceRequest(
            HttpServletRequest request, HttpServletResponse response) {
        try {
            ToolSession toolSession = SessionManager.getCurrentToolSession();
            ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
            ContentEntity contentEntity = pipe.getContentEntity();
            
            String packageId = (String)contentEntity.getProperties().get(PROP_SCORMCLOUD_PACKAGE_ID);

            //This also works, but displays course inside the portlet window, instead of a new browser
            //window (which makes it less of a true preview)
            //ScormCloudPackage pkg = logic.getPackageById(packageId);
            //String relativeRedirectUrl = endToolHelperSession(toolSession, pipe);
            //String absoluteRedirectUrl = getAbsoluteUrlTo(request, relativeRedirectUrl);
            //String previewUrl = logic.getPackagePreviewUrl(pkg, absoluteRedirectUrl);
            //response.sendRedirect(previewUrl);
            
            String returnUrl = endToolHelperSession(toolSession, pipe);
            returnUrl = URLEncoder.encode(returnUrl);
            //response.sendRedirect(getAbsoluteUrlTo(request, "/scormcloud-tool/controller?action=viewPackageProperties&id=" + packageId));
            response.sendRedirect("/scormcloud-tool/controller?action=viewPackageProperties&id=" + packageId + "&returnUrl=" + returnUrl);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    private void processPreviewPackageResourceRequest(
            HttpServletRequest request, HttpServletResponse response) {
        try {
            ToolSession toolSession = SessionManager.getCurrentToolSession();
            ResourceToolActionPipe pipe = (ResourceToolActionPipe) toolSession.getAttribute(ResourceToolAction.ACTION_PIPE);
            ContentEntity contentEntity = pipe.getContentEntity();
            
            String packageId = (String)contentEntity.getProperties().get(PROP_SCORMCLOUD_PACKAGE_ID);

            //This also works, but displays course inside the portlet window, instead of a new browser
            //window (which makes it less of a true preview)
            //ScormCloudPackage pkg = logic.getPackageById(packageId);
            //String relativeRedirectUrl = endToolHelperSession(toolSession, pipe);
            //String absoluteRedirectUrl = getAbsoluteUrlTo(request, relativeRedirectUrl);
            //String previewUrl = logic.getPackagePreviewUrl(pkg, absoluteRedirectUrl);
            //response.sendRedirect(previewUrl);
            
            endToolHelperSession(toolSession, pipe);
            response.sendRedirect(getAbsoluteUrlTo(request, "/scormcloud-tool/controller?action=previewPackage&id=" + packageId));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getLaunchHistoryInfoXml(
            HttpServletRequest request, HttpServletResponse response) {
        String registrationId = request.getParameter("regId");
        String launchId = request.getParameter("launchId");
        ScormCloudRegistration reg = logic.getRegistrationById(registrationId);
        return logic.getLaunchInfoXml(reg, launchId);
    }

    private void processViewLaunchHistoryReport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String registrationId = request.getParameter("registrationId");
        ScormCloudRegistration reg = logic.getRegistrationById(registrationId);
        ScormCloudPackage pkg = logic.getPackageById(reg.getPackageId());
        request.setAttribute("reg", reg);
        request.setAttribute("pkg", pkg);
        request.setAttribute("launchHistoryReport", logic.getLaunchHistoryReport(reg));
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_LAUNCH_HISTORY_REPORT);
        rd.forward(request, response);
    }

    private void processViewActivityReportRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String registrationId = request.getParameter("registrationId");
        ScormCloudRegistration reg = logic.getRegistrationById(registrationId);
        ScormCloudPackage pkg = logic.getPackageById(reg.getPackageId());
        Document reportXml = logic.getRegistrationReport(reg);
        request.setAttribute("reg", reg);
        request.setAttribute("pkg", pkg);
        request.setAttribute("activityReport",
                (new ActivityReporter()).createReport(reportXml));
        RequestDispatcher rd = request.getRequestDispatcher(PAGE_ACTIVITY_REPORT);
        rd.forward(request, response);
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
            String isMasterConfigStr = request.getParameter("is-master-config");
            Boolean isMasterConfig = Boolean.parseBoolean(isMasterConfigStr);

            ScormCloudConfiguration config = logic.getScormCloudConfiguration();
            if(config == null || (config.getIsMasterConfig() && !isMasterConfig)){
                config = new ScormCloudConfiguration();
            }
            config.setContext(extLogic.getCurrentContext());
            config.setIsMasterConfig(isMasterConfig);
            config.setAppId(appId);
            config.setSecretKey(secretKey);
            config.setServiceUrl(serviceUrl);
            logic.saveScormCloudConfiguration(config);
	    }
	    RequestDispatcher rd = request.getRequestDispatcher(PAGE_WELCOME);
	    rd.forward(request, response);
    }

    private void processViewPackagePropertiesRequest(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String packageId = request.getParameter("id");
        String returnUrl = request.getParameter("returnUrl");
        
        ScormCloudPackage pkg = logic.getPackageById(packageId);
        String packagePropertiesUrl = logic.getPackagePropertiesUrl(pkg, 
                getAbsoluteUrlTo(request, "/scormcloud-tool/css/PackagePropertyEditor.css"));
        
        request.setAttribute("pkg", pkg);
        request.setAttribute("packagePropertiesUrl", packagePropertiesUrl);
        if(returnUrl != null){
            request.setAttribute("returnUrl", returnUrl);
        } else {
            request.setAttribute("returnUrl", request.getHeader("referer"));
        }
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
        processViewRegistrationsRequest(request, response);
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
            toolBean.getMessages().add("Updated " + itemsUpdated + " items");
        }
    }

    private void processDeleteRegistrationsRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ScormCloudToolBean bean = toolBean;
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
        ScormCloudToolBean bean = toolBean;
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
        
        HashMap<String, Object> propertyMap = new HashMap<String, Object>();
        if (!isNullOrEmpty(packageId)) {
            propertyMap.put("packageId", packageId);
            request.setAttribute("pkg", logic.getPackageById(packageId));
        }
        if (!isNullOrEmpty(userSearch)) { 
            propertyMap.put("userDisplayName", "%"+userSearch+"%");
            request.setAttribute("userSearch", userSearch);
        }
        if (!isNullOrEmpty(assignmentSearch)){ 
            propertyMap.put("assignmentName", "%"+assignmentSearch+"%");
            request.setAttribute("assignmentSearch", assignmentSearch);
        }
        
        List<ScormCloudRegistration> regList = logic.getRegistrationsWherePropertiesLike(propertyMap);
        
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
	       
	       response.sendRedirect(endToolHelperSession(toolSession, pipe));
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
            return;
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
	
	private String endToolHelperSession(ToolSession toolSession, ResourceToolActionPipe pipe) {
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
        return url;
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
       
       response.sendRedirect(endToolHelperSession(toolSession, pipe));
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

        //If in an assignment context, make sure we the 
        //assignment can still accept submissions from this user
        if(reg.getAssignmentId() != null){
            if(!extLogic.canSubmitAssignment(reg.getContext(), reg.getAssignmentId())){
                sendToMessagePage(request, response, 
                        "Launch Not Allowed", 
                        "We're sorry, but this resource appears to be associated with " +
                        "an assignment which cannot recieve any more submissions from " +
                        "user " + reg.getUserName());
                    return;
            }
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
	    return getAbsoluteUrlTo(request, request.getRequestURI());
	}
	
	private String getAbsoluteUrlTo (HttpServletRequest request, String relativeUrl) throws Exception {
	    URL theUrl = new URL(request.getScheme(), 
	            request.getServerName(), 
	            request.getServerPort(),
	            relativeUrl);
	    return theUrl.toString();
	}

	public static String getEncodedParam(String paramName, String paramVal) throws Exception {
        return URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(paramVal, "UTF-8");
    }
	
	private boolean isNullOrEmpty(String str){
	    return (str == null || str.length() == 0);
	}
}
