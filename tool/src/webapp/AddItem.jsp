<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.sakaiproject.scormcloud.tool.ScormCloudItemsBean" %>
<%@ page import="org.sakaiproject.scormcloud.model.ScormCloudItem" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%
	// Get the backing bean from the spring context
	WebApplicationContext context = 
		WebApplicationContextUtils.getWebApplicationContext(application);
	ScormCloudItemsBean bean = (ScormCloudItemsBean) context.getBean("itemsBean");

	// Get POST and GET variables
	ScormCloudItem item = new ScormCloudItem(); // create an empty item
	if ( (request.getParameter("add-update-item") != null) ) {
		// we are adding or updating an item
		boolean error = false;
		String itemTitle = "";
		if ( (request.getParameter("item-title") != null) ) {
			itemTitle = request.getParameter("item-title");
		}

		Boolean itemHidden = Boolean.FALSE;
		if ( (request.getParameter("item-hidden") != null) ) {
			itemHidden = Boolean.TRUE;
		}

		if ( (request.getParameter("id") != null) ) {
			// updating an item
			Long id = new Long(request.getParameter("id"));
			item = bean.getItemById(id);
			if (item == null) {
				error = true;
				bean.messages.add("Invalid id: Cannot find item with id: " + id);
				item = new ScormCloudItem(); // create an empty item
			} else {
				bean.messages.add("Updated item: " + item.getTitle());
			}
		} else {
			if (itemTitle.equals("")) {
				error = true;
				bean.messages.add("Cannot create item, item title is required");
			} else {
				bean.messages.add("Added new item: " + itemTitle);
			}
		}

		item.setTitle(itemTitle);
		item.setHidden(itemHidden);

		if (!error) {
			bean.addOrUpdateItem(item);

			// redirect to the startpage
			response.sendRedirect("StartPage.jsp");
			return; // stop page execution here
		}
	} else {
		// First arrived at this page
		if ( (request.getParameter("id") != null) ) {
			// updating an item
			Long id = new Long(request.getParameter("id"));
			item = bean.getItemById(id);
			if (item == null) {
				bean.messages.add("Invalid id: Cannot find item with id: " + id);
			}
		} else {
			// adding new item, set the defaults
			item.setTitle("");
			item.setHidden(Boolean.TRUE);
		}
	}
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
	<a href="StartPage.jsp">List Items</a>
</div>

<h3 class="insColor insBak insBorder">Add/Update Item</h3>

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

<form name="addUpdateItemForm" action="AddItem.jsp" method="post">
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
		<input name="add-update-item" type="submit" value="Add/Update Item" />
	</p>
</form>

</div>
</body>
</html>
