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

	if (request.getParameterValues("delete-items") != null) {
		// user clicked the submit
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
	}

	// so we can format the creation date
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<title>SCORM Cloud Packages</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<div class="navIntraTool">
	<a href="AddItem.jsp">Import Package</a>
</div>

<h3 class="insColor insBak insBorder">SCORM Cloud Packages</h3>

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

<form name="listItemsForm" action="StartPage.jsp" method="post">
	<table class="listHier">
		<thead>
			<tr>
				<th class="firstHeader"></th>
				<th class="secondHeader">Title</th>
				<th class="thirdHeader">Hidden</th>
				<th class="fourthHeader">Creation Date</th>
			</tr>
		</thead>
		<tbody>
		<% 
		List packages = bean.getAllVisiblePackages();
		for (int i=0; i<packages.size(); i++) {
			ScormCloudPackage pkg = (ScormCloudPackage) packages.get(i);
			boolean deletable = bean.canDelete(pkg);
		%>
			<tr>
				<td class="firstColumn">
					<% if (deletable) { %>
						<input name="select-item" value="<%= pkg.getId() %>" type="checkbox" />
					<% } %>
				</td>
				<td class="secondColumn">
					<% if (deletable) { %>
						<a href="AddItem.jsp?id=<%= pkg.getId() %>">
							<%= pkg.getTitle() %>
						</a>
					<% } else { %>
						<%= pkg.getTitle() %>
					<% }%>						
				</td>
				<td class="thirdColumn">
					<% if (pkg.getHidden().booleanValue()) { %>
						<input name="item-hidden" type="checkbox" disabled="true" checked="true" />
					<% } else { %>
						<input name="item-hidden" type="checkbox" disabled="true" />
					<% }%>
				</td>
				<td class="fourthColumn">
					<%= df.format(pkg.getDateCreated()) %>
				</td>
			</tr>
		<% } %>
		</tbody>
	</table>

	<p class="act">
		<input name="delete-items" type="submit" value="Delete" />
	</p>
</form>

</div>
</body>
</html>
