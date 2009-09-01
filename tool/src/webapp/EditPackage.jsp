<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.rusticisoftware.scormcloud.tool.ScormCloudToolBean" %>
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
<title>SCORM Cloud Package Properties Editor</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<div class="navIntraTool">
    <a href="controller?action=viewPackages">List Resources</a>
    <a href="controller?action=viewRegistrations">Search Registrations</a>
    <c:if test="${canConfigure}">
	    <a href="controller?action=viewCloudConfiguration">Configure Plugin</a>
    </c:if>
</div>

<h3 class="insColor insBak insBorder">Editing SCORM Cloud Package Properties for ${pkg.title}</h3>

<div class="instruction">When you are finished, please click <a href="javascript:history.back()">here</a> to go back.</div>


<c:if test="${fn:length(bean.messages) > 0}">
    <div class="alertMessage">
        <ul style="margin:0px;">
        <c:forEach var="msg" items="${bean.messages}">
            <li>${msg}</li>
        </c:forEach>
        </ul>
    </div>
    <% bean.messages.clear(); %>
</c:if>

<iframe id="packagePropertiesEditor" frameborder="no" src="${packagePropertiesUrl}" />


</div>
</body>
</html>