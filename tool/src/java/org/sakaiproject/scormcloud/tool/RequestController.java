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
				output.println(action);
				output.println(processImportRequest(request, response));
				return;
//				ClientData client = getClientData(request.getParameter("appid"));
//				request.setAttribute("client", client);
//				RequestDispatcher rd = request.getRequestDispatcher("ClientDetail.jsp");
//				rd.forward(request, response);
			}
			
			if(action.equals("launchPackage")){
				String packageId = request.getParameter("id");
				log.debug("action launchPackage requested with packageId = " + packageId);
				
				ScormCloudPackage pkg = getScormCloudPackagesBean().getPackageById(packageId);
				if(pkg == null){
					log.debug("Error in launchPackage action, no package with id = " + packageId + " found!");
					request.setAttribute("errorMessage", "Package with id " + packageId + " not found!");
					RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
					rd.forward(request, response);
				}
				
				ScormCloudRegistration reg = getScormCloudPackagesBean().findOrCreateUserRegistrationFor(pkg);
				String launchUrl = getScormCloudPackagesBean().getLaunchUrl(reg);
				
				log.debug("launchUrl = " + launchUrl);
				request.setAttribute("url", launchUrl);
				RequestDispatcher rd = request.getRequestDispatcher("Launch.jsp");
				rd.forward(request, response);
			}
			
			if(action.equals("deleteClientData")){
//				(new AccountManager()).deleteClientData(request.getParameter("appid"));
//				response.sendRedirect("ClientSummary.jsp");
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
	
	public String processImportRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
        
        return pkg.getScormCloudId();
	}

	private ScormCloudPackagesBean getScormCloudPackagesBean()
	{
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return (ScormCloudPackagesBean)context.getBean("packagesBean");
	}
	
}
