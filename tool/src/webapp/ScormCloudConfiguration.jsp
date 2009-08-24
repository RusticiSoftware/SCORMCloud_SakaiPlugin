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
    <a href="PackageList.jsp">List Packages</a>
    <a href="ImportPackage.jsp">Import Package</a>
    <a href="controller?action=configureCloudPlugin">Configure Plugin</a>
</div>

<h3 class="insColor insBak insBorder">SCORM Cloud Configuration</h3>

<div class="instruction">Enter your AppId and Secret Key below to activate the SCORM Cloud Plugin.</div>
<form name="cloudConfigForm" id="cloudConfigForm" action="controller?action=configureCloudPlugin" method="post">
	<table>
		<tr>
			<td>AppId</td>
			<td><input id="appId" name="appId" type="text" value="${config.appId}" size="40" /></td>
		</tr>
		<tr>
			<td>Secret Key</td>
			<td><input id="secretKey" name="secretKey" type="text" value="${config.secretKey}" size="40" /></td>
		</tr>
		<tr>
			<td>Service URL</td>
			<td><input id="serviceUrl" name="serviceUrl" type="text" value="${config.serviceUrl}" size="40" /></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td><input type="submit" name="submit" value="Submit" /></td>
			<td><input type="submit" name="cancel" value="Cancel" /></td>
		</tr>
	</table>
</form>

</div>
</body>
</html>