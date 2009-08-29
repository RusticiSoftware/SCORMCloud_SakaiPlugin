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
<title>SCORM Cloud Launch Pad</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>" style="padding-left:10px; padding-top:10px">
<div class="portletBody">


<h3 class="insColor insBak insBorder">Launching SCORM Cloud Registration</h3>

	<script type="text/javascript">
         window.open("${url}", "launchWindow", "");
    </script>
    
    <%--<c:if test="${resourceLink}">
    	<script type="text/javascript">
    		if(launchWindow){
    			window.location = "Closer.html";
    		}
    	</script>
    </c:if>--%>
    
<div class="instruction" style="padding-top:10px">
	Your course has launched in a new window. When you have finished, please click 
	<a href="javascript:history.back()">here</a> to go back, or close this window.
</div>

</div>
</body>
</html>

<!-- <script>
   history.back()
</script> -->