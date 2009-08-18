<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.sakaiproject.scormcloud.tool.ScormCloudPackagesBean" %>
<%@ page import="org.sakaiproject.scormcloud.model.ScormCloudPackage" %>
<%@ page import="org.sakaiproject.scormcloud.model.ScormCloudRegistration" %>
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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css"/>
<link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css"/>
<link media="all" href="css/ScormCloud.css" rel="stylesheet" type="text/css"/>
<title>SCORM Cloud Registrations</title>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<div class="portletBody">

<div class="navIntraTool">
    <a href="PackageList.jsp">List Packages</a>
    <a href="ImportPackage.jsp">Import Package</a>
</div>

<h3 class="insColor insBak insBorder">SCORM Cloud Registrations for Package ${pkg.title}</h3>

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


<div class="instruction">Hello, ${bean.currentUserDisplayName}</div>

<form name="listItemsForm" action="controller?action=processRegistrationListAction" method="post">
	<input type="hidden" name="pkgId" value="${pkg.id}" />
    <table class="listHier">
        <thead>
            <tr>
                <th class="firstHeader"></th>
                <th class="secondHeader">Username</th>
                <th class="thirdHeader">SCORM Cloud ID</th>
                <th class="fourthColumn">Complete</th>
                <th class="fifthColumn">Success</th>
                <th class="sixthColumn">Score</th>
                <th class="seventhColumn">Time Spent</th>
                <th class="eighthColumn">Creation Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="reg" items="${regList}">
            <% ScormCloudRegistration reg = (ScormCloudRegistration)pageContext.getAttribute("reg"); %>
                <c:set var="deletable"><%= bean.canDelete(reg) %></c:set>
                <tr>
                    <td class="firstColumn">
                        <c:if test="${deletable}">
                            <input name="select-item" value="${reg.id}" type="checkbox" />
                        </c:if>
                    </td>
                    <td class="secondColumn">
                        <c:choose>
                            <c:when test="${deletable}">
                                <a href="controller?action=viewDetailedRegistrationReport&id=${reg.id}">
                                    ${reg.userName}
                                </a>
                            </c:when><c:otherwise>
                                ${reg.userName}
                            </c:otherwise>      
                        </c:choose>             
                    </td>
                    <td class="thirdColumn">
                        <span>${reg.scormCloudId}</span>
                    </td>
                    <%-- <td class="fourthColumn">
                        <c:choose>
                            <c:when test="${reg.hidden}">
                                <input name="item-hidden" type="checkbox" disabled="true" checked="true" />
                            </c:when><c:otherwise>
                                <input name="item-hidden" type="checkbox" disabled="true" />
                            </c:otherwise>
                        </c:choose>
                    </td> --%>
                    <td class="fourthColumn">
                    	<span>${reg.complete}</span>
                    </td>
                    <td class="fifthColumn">
                    	<span>${reg.success}</span>
                    </td>
                    <td class="sixthColumn">
                    	<span>${reg.score}</span>
                    </td>
                    <td class="seventhColumn">
                    	<span>${reg.totalTime}</span>
                    </td>
                    <td class="eighthColumn">
                        <fmt:formatDate value="${reg.dateCreated}" type="both" 
                            dateStyle="medium" timeStyle="medium" />
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p class="act">
    	<input name="update-items" type="submit" value="Update Results" />
        <input name="delete-items" type="submit" value="Delete" />
    </p>
</form>

</div>
</body>
</html>