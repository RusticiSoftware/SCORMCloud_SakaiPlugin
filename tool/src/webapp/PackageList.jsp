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
    <a href="PackageList.jsp">List Packages</a>
    <a href="ImportPackage.jsp">Import Package</a>
</div>

<h3 class="insColor insBak insBorder">SCORM Cloud Packages</h3>

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

<form name="listItemsForm" action="StartPage.jsp" method="post">
    <table class="listHier">
        <thead>
            <tr>
                <th class="firstHeader"></th>
                <th class="secondHeader">Title</th>
                <th class="thirdHeader">SCORM Cloud ID</th>
                <th class="fourthHeader">Hidden</th>
                <th class="fifthHeader">Creation Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="pkg" items="${bean.allVisiblePackages}">
                <% ScormCloudPackage pkg = (ScormCloudPackage)pageContext.getAttribute("pkg"); %>
                <c:set var="deletable"><%= bean.canDelete(pkg) %></c:set>
                <tr>
                    <td class="firstColumn">
                        <c:if test="${deletable}">
                            <input name="select-item" value="${pkg.id}" type="checkbox" />
                        </c:if>
                    </td>
                    <td class="secondColumn">
                        <c:choose>
                            <c:when test="${deletable}">
                                <a href="controller?action=launchPackage&id=${pkg.id}">
                                    ${pkg.title}
                                </a>
                            </c:when><c:otherwise>
                                ${pkg.title}
                            </c:otherwise>      
                        </c:choose>             
                    </td>
                    <td class="thirdColumn">
                        <span>${pkg.scormCloudId}</span>
                    </td>
                    <td class="fourthColumn">
                        <c:choose>
                            <c:when test="${pkg.hidden}">
                                <input name="item-hidden" type="checkbox" disabled="true" checked="true" />
                            </c:when><c:otherwise>
                                <input name="item-hidden" type="checkbox" disabled="true" />
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="fifthColumn">
                        <fmt:formatDate value="${pkg.dateCreated}" type="both" 
                            dateStyle="medium" timeStyle="medium" />
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p class="act">
        <input name="delete-items" type="submit" value="Delete" />
    </p>
</form>

</div>
</body>
</html>
