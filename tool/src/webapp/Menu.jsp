<%
	pageContext.setAttribute("isAdmin", bean.isCurrentUserSakaiAdmin() || bean.isCurrentUserPluginAdmin());
	pageContext.setAttribute("isConfigured", bean.isPluginConfigured());
	pageContext.setAttribute("canConfigure", bean.canConfigurePlugin());
%>
<c:if test="${isAdmin}">
	<div class="navIntraTool">
	    <a href="controller?action=viewPackages">List Resources</a>
	    <a href="controller?action=viewRegistrations">Search Registrations</a>
	    <c:if test="${canConfigure}">
		    <a href="controller?action=viewCloudConfiguration">Configure Plugin</a>
		    <c:choose>
			    <c:when test="${isConfigured}">
			    	<a href="controller?action=viewUsage">View Usage</a>
			    </c:when><c:otherwise>
			    	<a href="controller?action=viewSignup">Sign Up</a>
			    </c:otherwise>
		    </c:choose>
	    </c:if>
	    <a href="controller?action=viewOverallGroupReport">Overall Report</a>
	</div>
</c:if>