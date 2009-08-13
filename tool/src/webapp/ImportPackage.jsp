<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
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
	ScormCloudPackagesBean bean = (ScormCloudPackagesBean) context.getBean("packagesBean");
	
	ScormCloudPackage item = new ScormCloudPackage();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<title>ScormCloud Add/Update Item</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<div class="navIntraTool">
	<a href="PackageList.jsp">List Packages</a>
</div>

<h3 class="insColor insBak insBorder">Import Package</h3>

<% if (bean.messages.size() > 0) { %>
<div class="alertMessage">
	<ul style="margin:0px;">
	<% for (int i=0; i<bean.messages.size(); i++) { %>
		<li><%= (String) bean.messages.get(i) %></li>
	<% } %>
	</ul>
</div>
<% } bean.messages.clear(); %>

<div class="instruction">Hello, <%= bean.getCurrentUserDisplayName() %></div>

<form name="importPackageForm" action="controller?action=importPackage" method="post" enctype="multipart/form-data">
	<table border="0" cellspacing="0" class="chefEditItem">
		<tbody>
			<tr>
				<td>Title</td>
				<td>
					<input name="item-title" id="item-title" type="text" size="70" value="<%= item.getTitle() %>" />
					<script type="text/javascript" language="JavaScript">
					<!--
						document.getElementById("item-title").focus();
					// -->
					</script>
				</td>
			</tr>
			<tr>
				<td>File</td>
				<td>
					<input id="filedata" name="filedata" type="file" size="70" />
				</td>
			</tr>
			<tr>
				<td>Hidden</td>
				<td>
					<%
					String checkVal = "checked='true'";
					if (item.getHidden() != null && !item.getHidden().booleanValue()) {
						checkVal = "";
					}
					%>
					<input name="item-hidden" title="Hide this from other users" 
							type="checkbox" value="true" <%= checkVal %> />
				</td>
			</tr>
		</tbody>
	</table>

	<p class="act">
		<% if (item.getId() != null) { %>
			<input name="id" type="hidden" value="<%= item.getId() %>" />
		<% } %>
		<input name="import-package" type="submit" value="Import" />
	</p>
</form>

</div>
</body>
</html>