package org.sakaiproject.scormcloud.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.scormcloud.logic.ExternalLogic;
import org.sakaiproject.scormcloud.logic.ScormCloudLogic;
import org.sakaiproject.scormcloud.model.ScormCloudConfiguration;
import org.sakaiproject.scormcloud.model.ScormCloudPackage;
import org.sakaiproject.scormcloud.model.ScormCloudRegistration;

public class ScormCloudPackagesBean {
	private static Log log = LogFactory.getLog(ScormCloudPackagesBean.class);

	public ScormCloudPackage newItem = new ScormCloudPackage();
	public Map selectedIds = new HashMap();

	private ScormCloudLogic logic;
	public void setLogic(ScormCloudLogic logic) {
		this.logic = logic;
	}

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	public ArrayList messages = new ArrayList();
	public ArrayList getMessages() {
		return messages;
	}

	public void init() {
		log.debug("init");
	}

	public ScormCloudPackagesBean() {
		log.debug("constructor");
	}

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}

	public boolean isCurrentUserSakaiAdmin(){
	    return logic.isCurrentUserSakaiAdmin();
	}
	
	public boolean isCurrentUserPluginAdmin(){
	    return logic.isCurrentUserPluginAdmin();
	}
	
	public boolean canConfigurePlugin(){
	    return logic.canConfigurePlugin();
	}
	
	public boolean isPluginConfigured(){
	    return logic.isPluginConfigured();
	}
	
	public void ensureConfigured(HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
	    boolean configured = logic.isPluginConfigured();
        if(!configured){
            response.sendRedirect(RequestController.PAGE_WELCOME);
        }
	}
	
	public void allowOnlyAdmin(HttpServletRequest request, 
	        HttpServletResponse response) throws Exception { 
	    boolean admin = logic.isCurrentUserPluginAdmin() || logic.isCurrentUserSakaiAdmin();
	    if(!admin){
	        response.sendRedirect("controller?action=actionNotAllowed");
	    }
	}
	
	public void doPageChecks(HttpServletRequest request, 
            HttpServletResponse response) throws Exception { 
        ensureConfigured(request, response);
        allowOnlyAdmin(request, response);
    }
	
	public String formatSeconds(String seconds){
	    try {
	        int secondNum = Integer.parseInt(seconds);
	        if(secondNum > 30){
	            int minutes = (secondNum/30);
	            int remainingSeconds = (secondNum%30);
	            return minutes+" min " + remainingSeconds + " s";
	        } else {
	            return secondNum + " s";
	        }
	    } catch (NumberFormatException e){
	        return null;
	    }
	}
}
