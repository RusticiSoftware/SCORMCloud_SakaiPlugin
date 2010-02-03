<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.rusticisoftware.scormcloud.tool.ScormCloudToolBean" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%
    // Get the backing bean from the spring context
    WebApplicationContext context = 
        WebApplicationContextUtils.getWebApplicationContext(application);
    ScormCloudToolBean bean = (ScormCloudToolBean)context.getBean("scormCloudToolBean");

    bean.doPageChecks(request, response);
    
    pageContext.setAttribute("bean", bean);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<script type="text/javascript" src="javascript/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
        var extConfigurationString = '';
        var reportsHelperUrl = 'controller';
</script>
<script type="text/javascript" src="javascript/LaunchHistoryReport.js"></script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<link rel="Stylesheet" href="css/LaunchHistoryReport.css" />
<title>SCORM Cloud Registration Launch History Report</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<%@ include file="Menu.jsp" %>

<h3 class="insColor insBak insBorder">SCORM Cloud Registration Launch History Report</h3>

<div class="instruction">Displayed below is a list showing each of ${reg.userName}'s 
    launches of the resource ${pkg.title}. To view details about a specific launch, you can 
    expand using the plus symbol to the left of the launch time. When finished viewing, 
    you can click <a href="javascript:history.back()">here</a> to go back.</div>

<%@ include file="Messages.jsp" %>

<div id="launchHistoryReportContainer">
	<div id="historyInfo">
       <div class="info_title">Launches</div>
       <div id="historyDetails" class="history_details">
       		${launchHistoryReport}
       </div>
    </div>
</div>


</div>
</body>
</html>