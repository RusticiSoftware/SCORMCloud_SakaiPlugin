<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.rusticisoftware.scormcloud.tool.ScormCloudToolBean" %>
<%@ page import="com.rusticisoftware.scormcloud.model.ScormCloudPackage" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%
	// Get the backing bean from the spring context
	WebApplicationContext context = 
		WebApplicationContextUtils.getWebApplicationContext(application);
	ScormCloudToolBean bean = (ScormCloudToolBean) context.getBean("scormCloudToolBean");

    bean.doPageChecks(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<script type="text/javascript">
	function validateForm(){
		var filedata = document.getElementById('filedata');
		var titleInput = document.getElementById('package-title');
		if(filedata.value === null || filedata.value == ""){
			alert("Please choose a file to upload");
			return false;
		} 
		if(titleInput.value === null || titleInput.value == ""){
			alert("Please provide a title");
			return false;
		} 
		return true;
	}
</script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<title>SCORM Cloud Import Package</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<h3 class="insColor insBak insBorder">Update Package for [Existing Package Title Here]</h3>

<div class="instruction">Choose a zip file containing SCORM content</div>

<form name="importPackageForm" action="controller?action=importPackage" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">
	<input name="helper" id="helper" type="hidden" value="${param.helper}" />
	<p class="shorttext">
			<span class="reqStar">*</span>
			<span style="padding-right: 20px">	
				File	
			</span>
			<input id="filedata" name="filedata" type="file" size="30" />
	</p>
	
	<p class="shorttext">
			<span class="reqStar">*</span>
			<span style="padding-right: 20px">
				New Title
			</span>	
			<input name="package-title" id="package-title" type="text" size="28" value="[Existing Package Title Here]" />
	</p>
	<br />
	
	<h4>New File Behavior</h4>
	<p class="checkbox  indnt2">
		<input type="radio" 
			name="contribute-to-assigment-grade"
			id="contribute-to-assigment-grade-false"
			value="false" 
			checked="checked" />
		<label for="contribute-to-assigment-grade-false">Overwrite existing files in place.</label>
		<br />
		<input type="radio" 
			name="contribute-to-assigment-grade"
			id="contribute-to-assigment-grade-true"
			value="true" />
		<label for="contribute-to-assigment-grade-true">Maintain existing files, and create a new version of the course.</label>
	</p>
	
	<h4>Existing Registration Behavior</h4>
	<p class="checkbox  indnt2">
		<input type="radio" 
			name="allow-non-assignment-launch"
			id="allow-non-assignment-launch-false"
			value="false" 
			checked="checked" />
		<label for="allow-non-assignment-launch-false">Do NOT force existing registrations to restart with this update. (If you're creating a new version of a course, existing registrations will be allowed to finish their current version.)</label>
		<br />
		<input type="radio" 
			name="allow-non-assignment-launch"
			id="allow-non-assignment-launch-true"
			value="true" />
		<label for="allow-non-assignment-launch-true">Force existing registrations to restart with this update. (If you're creating a new version of a course, registrations will be reset to the new version.)</label>
	</p>

	<p class="act">
		<input name="import-package" type="submit" value="Update" />
	</p>
</form>

</div>
</body>
</html>