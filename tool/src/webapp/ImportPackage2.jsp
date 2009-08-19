<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%
		response.setContentType("text/html; charset=UTF-8");
%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link href="<c:out value="${sakai_skin_base}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <link href="<c:out value="${sakai_skin}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <title><%= org.sakaiproject.tool.cover.ToolManager.getCurrentTool().getTitle()%></title>
    <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js">
    </script>
  <%
      String panelId = request.getParameter("panel");
      if (panelId == null) {
         panelId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
      }

  %>

  <script type="text/javascript" >
   function resetHeight() {
      setMainFrameHeight('<%= org.sakaiproject.util.Validator.escapeJavascript(panelId)%>');
   }

   function loaded() {
      resetHeight();
      parent.updCourier(doubleDeep, ignoreCourier);
      if (parent.resetHeight) {
         parent.resetHeight();
      }
   }
  </script>
  </head>

  <body onload="loaded();">
      <div class="portletBody">
         <h3>Select a SCORM or IMS content package to upload.</h3>
         <p class="instruction indnt2">

             The following content versions are supported:
                <ul>
                <li>SCORM 1.2</li>
                <li>SCORM 2004 3rd Edition including SCORM run time environment, and all SCORM sequencing and navigation behaviour.</li>
                <li>IMS Content Packages</li>
                </ul>
         </p>
         <form method="post" action="controller?action=importPackage&helper=true" enctype="multipart/form-data">
             <input type="file" name="file" /> <input class="active" accesskey="s" type="submit" value="Upload"/>
         </form>
      </div>
   </body>
</html>