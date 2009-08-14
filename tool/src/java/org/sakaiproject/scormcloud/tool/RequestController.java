package org.sakaiproject.scormcloud.tool;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class RequestController extends HttpServlet {
	private static Log log = LogFactory.getLog(RequestController.class);

	private static final long serialVersionUID = 1L;

	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try{
			PrintWriter output = response.getWriter();
			String action = request.getParameter("action");
			
			if(action == null || action.length() < 1){
				output.println("error: No action specified.");
				return;
			}
			
			if(action.equals("importPackage")){
				processImportRequest(request, response);
			}
			if(action.equals("launchPackage")){
				processLaunchRequest(request, response);
			}
			if(action.equals("updatePackage")){
				/* Not implemented yet */
			}
			if(action.equals("updateRegSummaryData")){
				/* Not implemented yet */
			}
			if(action.equals("showPackageProperties")){
				/* Not implemented yet */
			}
			
			
			output.println("error: Action " + action + " not found");
		}
		catch (Exception e){
			throw new ServletException(e);
		}
	}

	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
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

        //Send the user back to the package list page
		response.sendRedirect("PackageList.jsp");
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
		String launchUrl = pkgsBean.getLaunchUrl(reg);
		
		//Forward the user to the launch page, now that a registration exists for them
		log.debug("launchUrl = " + launchUrl);
		request.setAttribute("url", launchUrl);
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
	
}
