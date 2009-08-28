<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.sakaiproject.scormcloud.tool.ScormCloudPackagesBean" %>
<%@ page import="org.sakaiproject.scormcloud.model.ScormCloudPackage" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%
    // Get the backing bean from the spring context
    WebApplicationContext context = 
        WebApplicationContextUtils.getWebApplicationContext(application);
    ScormCloudPackagesBean bean = (ScormCloudPackagesBean)context.getBean("packagesBean");
    pageContext.setAttribute("bean", bean);
    pageContext.setAttribute("isAdmin", bean.isCurrentUserSakaiAdmin() || bean.isCurrentUserPluginAdmin());
    pageContext.setAttribute("isConfigured", bean.isPluginConfigured());
    pageContext.setAttribute("canConfigure", bean.canConfigurePlugin());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<title>SCORM Cloud Plugin</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">	

	<c:if test="${isAdmin}">
		<div class="navIntraTool">
		    <a href="controller?action=viewPackages">List Resources</a>
		    <a href="controller?action=viewRegistrations">List Registrations</a>
		    <c:if test="${canConfigure}">
			    <a href="controller?action=viewCloudConfiguration">Configure Plugin</a>
		    </c:if>
		</div>
	</c:if>
	
	<h2 class="insColor insBak insBorder">SCORM Cloud Plugin by Rustici Software</h2>
	
	<table>
		<tr>
			<td>
				<img class="messageIcon" src="images/icon_cloud.jpg" />
			</td>
			<td style="vertical-align:top">
				<div style="padding-left:20px">
					<h3 class="messageTitle">Welcome to the SCORM Cloud Plugin for Sakai</h3>
					<div class="instruction">
						<c:choose>
							<c:when test="${isAdmin}">
								<c:choose>
									<c:when test="${isConfigured}">
										Please use the links above to view detailed reporting about your
										learner's interactions with the SCORM resources that have been
										utilized in your assignments.
									</c:when><c:otherwise>
										<c:choose>
											<c:when test="${canConfigure}">
												Please configure the plugin using the link above in the navigation toolbar.
											</c:when><c:otherwise>
												Before you can use any of the features of the plugin, it must be configured
												by your local Sakai administrator or a role that has been granted the ability of "scormcloud.configure". 
											</c:otherwise>
										</c:choose>
										Once the plugin is configured, you can use the links above to view detailed 
										reporting of your learner's interaction with the SCORM resources that have 
										been attached to assignments.
									</c:otherwise>
								</c:choose>
								Documentation on the usage of the SCORM Cloud plugin can be found <a href="Documentation.html">here.</a>
							</c:when><c:otherwise>
								The SCORM Cloud Plugin for Sakai is currently ${isConfigured ? "" : "not"} active for this site.
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</td>
		</tr>
	</table>

</div>
</body>
</html>

<!-- <script>
   history.back()
</script> -->